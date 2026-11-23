package com.sliit.betterme.ui.hydration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sliit.betterme.R
import com.sliit.betterme.ui.hydration.views.HalfCircleGaugeView
import java.util.Calendar
import java.util.concurrent.TimeUnit

class TodayHydrationFragment : Fragment(R.layout.fragment_hydration_today) {
    private val vm: HydrationViewModel by activityViewModels()
    private var goalShown = false
    private var selectedCupMl = 200
    private var selectedDrinkLabel = "Water"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_hydration_today, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gauge = view.findViewById<HalfCircleGaugeView>(R.id.gauge)
        val tvAmount = view.findViewById<TextView>(R.id.tvAmount)
        val tvGoal = view.findViewById<TextView>(R.id.tvGoal)

        val rv = view.findViewById<RecyclerView>(R.id.rvHistory)
        val empty = view.findViewById<View>(R.id.tvEmptyHistory)
        val btnViewAll = view.findViewById<TextView>(R.id.tvViewAll)
        val adapter = HydrationHistoryAdapter(
            onEdit = { entry -> showEditDialog(entry) },
            onDelete = { entry -> vm.deleteEntry(entry.id) }
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        btnViewAll.setOnClickListener {
            (parentFragment?.view?.findViewById<ViewPager2>(R.id.viewPager))?.currentItem = 1 // History tab
        }

        vm.goal.observe(viewLifecycleOwner){ g ->
            gauge.setGoal(g)
            tvGoal.text = "/${g} mL"
        }
        vm.totalToday.observe(viewLifecycleOwner){ total ->
            gauge.setValue(total)
            tvAmount.text = total.toString()
            val goal = vm.goal.value ?: 0
            if (!goalShown && goal > 0 && total >= goal){
                goalShown = true
                GoalAchievedDialog().show(parentFragmentManager, "goal")
            }
        }
        vm.entriesToday.observe(viewLifecycleOwner){ list ->
            adapter.submit(list)
            empty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        view.findViewById<View>(R.id.btnAddIntake).setOnClickListener {
            showAmountAndSchedule(if (selectedDrinkLabel.equals("Water", true)) "water" else "drink")
        }
        view.findViewById<View>(R.id.btnCup).setOnClickListener {
            showCupDrinkSheet()
        }

        setFragmentResultListener(CupDrinkSheet.RESULT_KEY){ _, bundle ->
            if (bundle.containsKey("cup_size")){
                selectedCupMl = bundle.getInt("cup_size")
                view.findViewById<TextView>(R.id.btnAddIntake)?.text = "Drink (${selectedCupMl} mL)"
            }
            if (bundle.containsKey("drink_type")){
                selectedDrinkLabel = bundle.getString("drink_type") ?: "Water"
            }
        }
    }

    private fun showAmountAndSchedule(type: String) {
        val content = layoutInflater.inflate(R.layout.dialog_amount_time, null)
        val etAmount = content.findViewById<EditText>(R.id.etAmount)
        val tvDate = content.findViewById<TextView>(R.id.tvDate)
        val tvTime = content.findViewById<TextView>(R.id.tvTime)

        val cal = Calendar.getInstance()
        updateDateTimeLabels(tvDate, tvTime, cal)

        etAmount.setText(selectedCupMl.toString())

        tvDate.setOnClickListener { showWheelDate(cal){ updateDateTimeLabels(tvDate, tvTime, cal) } }
        tvTime.setOnClickListener { showWheelTime(cal){ updateDateTimeLabels(tvDate, tvTime, cal) } }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (type == "water") "Edit Water Intake" else "Drinking ${type.replaceFirstChar { it.uppercase() }}")
            .setView(content)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val amount = etAmount.text.toString().toIntOrNull() ?: selectedCupMl
                val entry = vm.addEntry(
                    name = if (type == "water") "Water" else selectedDrinkLabel,
                    type = type,
                    amount = amount,
                    timeMillis = cal.timeInMillis,
                    reminder = true
                )
                scheduleReminder(entry)
            }
            .show()
    }

    private fun showEditDialog(e: HydrationEntry) {
        val content = layoutInflater.inflate(R.layout.dialog_amount_time, null)
        val etAmount = content.findViewById<EditText>(R.id.etAmount)
        val tvDate = content.findViewById<TextView>(R.id.tvDate)
        val tvTime = content.findViewById<TextView>(R.id.tvTime)

        val cal = Calendar.getInstance(); cal.timeInMillis = e.timeMillis
        updateDateTimeLabels(tvDate, tvTime, cal)

        etAmount.setText(e.amountMl.toString())

        tvDate.setOnClickListener { showWheelDate(cal){ updateDateTimeLabels(tvDate, tvTime, cal) } }
        tvTime.setOnClickListener { showWheelTime(cal){ updateDateTimeLabels(tvDate, tvTime, cal) } }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Intake")
            .setView(content)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val amount = etAmount.text.toString().toIntOrNull() ?: e.amountMl
                vm.updateEntry(e.id, amount, cal.timeInMillis)
            }
            .show()
    }

    private fun updateDateTimeLabels(tvDate: TextView, tvTime: TextView, cal: Calendar) {
        tvDate.text = String.format("%1\$tb %1\$te, %1\$tY", cal)
        tvTime.text = String.format("%1\$tI:%1\$tM %1\$Tp", cal)
    }

    private fun showWheelDate(cal: Calendar, onPicked: ()->Unit){
        val v = layoutInflater.inflate(R.layout.dialog_wheel_date, null)
        val month = v.findViewById<android.widget.NumberPicker>(R.id.pMonth)
        val day = v.findViewById<android.widget.NumberPicker>(R.id.pDay)
        val year = v.findViewById<android.widget.NumberPicker>(R.id.pYear)
        val months = java.text.DateFormatSymbols().months.filter { it.isNotEmpty() }.toTypedArray()
        month.minValue = 0; month.maxValue = 11; month.displayedValues = months; month.value = cal.get(Calendar.MONTH)
        fun updateDays(){
            val c = Calendar.getInstance()
            c.set(Calendar.YEAR, year.value)
            c.set(Calendar.MONTH, month.value)
            c.set(Calendar.DAY_OF_MONTH, 1)
            val max = c.getActualMaximum(Calendar.DAY_OF_MONTH)
            day.minValue = 1; day.maxValue = max
            if (day.value > max) day.value = max
        }
        year.minValue = cal.get(Calendar.YEAR) - 2; year.maxValue = cal.get(Calendar.YEAR) + 2; year.value = cal.get(Calendar.YEAR)
        day.minValue = 1; day.maxValue = 31; day.value = cal.get(Calendar.DAY_OF_MONTH)
        month.setOnValueChangedListener { _,_,_ -> updateDays() }
        year.setOnValueChangedListener { _,_,_ -> updateDays() }
        updateDays()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Set Date")
            .setView(v)
            .setNegativeButton("Back", null)
            .setPositiveButton("OK"){_,_->
                cal.set(Calendar.YEAR, year.value)
                cal.set(Calendar.MONTH, month.value)
                cal.set(Calendar.DAY_OF_MONTH, day.value)
                onPicked()
            }.show()
    }
    private fun showWheelTime(cal: Calendar, onPicked: ()->Unit){
        val v = layoutInflater.inflate(R.layout.dialog_wheel_time, null)
        val pHour = v.findViewById<android.widget.NumberPicker>(R.id.pHour)
        val pMin = v.findViewById<android.widget.NumberPicker>(R.id.pMinute)
        val pAmPm = v.findViewById<android.widget.NumberPicker>(R.id.pAmPm)
        pHour.minValue = 1; pHour.maxValue = 12
        val hour12 = ((cal.get(Calendar.HOUR_OF_DAY) + 11) % 12) + 1
        pHour.value = hour12
        pMin.minValue = 0; pMin.maxValue = 59; pMin.setFormatter { String.format("%02d", it) }; pMin.value = cal.get(Calendar.MINUTE)
        pAmPm.minValue = 0; pAmPm.maxValue = 1; pAmPm.displayedValues = arrayOf("AM","PM"); pAmPm.value = if (cal.get(Calendar.AM_PM)==Calendar.AM) 0 else 1
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Set Hour")
            .setView(v)
            .setNegativeButton("Back", null)
            .setPositiveButton("OK"){_,_->
                var h = pHour.value % 12
                if (pAmPm.value == 1) h += 12
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, pMin.value)
                cal.set(Calendar.SECOND, 0)
                onPicked()
            }.show()
    }

    private fun scheduleReminder(entry: HydrationEntry){
        val delay = (entry.timeMillis - System.currentTimeMillis()).coerceAtLeast(0)
        val data = Data.Builder()
            .putString("title", "Drink reminder")
            .putString("message", "${entry.name} ${entry.amountMl} mL")
            .build()
        val req = OneTimeWorkRequestBuilder<IntakeReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(req)
    }

    private fun showCupDrinkSheet(){
        CupDrinkSheet().show(parentFragmentManager, "cup")
    }
}

class HydrationHistoryAdapter(
    private val onEdit: (HydrationEntry) -> Unit,
    private val onDelete: (HydrationEntry) -> Unit
) : RecyclerView.Adapter<HydrationHistoryVH>(){
    private val items = mutableListOf<HydrationEntry>()
    fun submit(list: List<HydrationEntry>){ items.clear(); items.addAll(list); notifyDataSetChanged() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HydrationHistoryVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_hydration_history, parent, false)
        return HydrationHistoryVH(v)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: HydrationHistoryVH, position: Int) {
        val item = items[position]
        holder.bind(item)
        val menuBtn = holder.itemView.findViewById<android.widget.ImageView>(R.id.btnMenu)
        menuBtn?.setOnClickListener { v ->
            val popup = android.widget.PopupMenu(v.context, v)
            popup.menu.add(0, 1, 0, "Edit")
            popup.menu.add(0, 2, 1, "Delete")
            popup.setOnMenuItemClickListener { mi ->
                when(mi.itemId){
                    1 -> onEdit(item)
                    2 -> onDelete(item)
                }
                true
            }
            popup.show()
        }
    }
}

class HydrationHistoryVH(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(e: HydrationEntry){
        (itemView.findViewById<TextView>(R.id.tvName)).text = e.name
        (itemView.findViewById<TextView>(R.id.tvAmount)).text = "${e.amountMl} mL"
        (itemView.findViewById<TextView>(R.id.tvTime)).text = android.text.format.DateFormat.format("h:mm a", e.timeMillis)
        val icon = if (e.type.equals("water", true)) R.drawable.ic_water else R.drawable.ic_drink
        itemView.findViewById<android.widget.ImageView>(R.id.imgIcon).setImageResource(icon)
    }
}
