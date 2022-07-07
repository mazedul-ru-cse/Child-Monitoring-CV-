package calender;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.childmonitoringchildversion.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {

        if(daysOfMonth.get(position).equals(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"")
                && CustomCalendar.currentDay == 50){
            holder.dayOfMonth.setText(daysOfMonth.get(position));
            holder.dayOfMonth.setBackgroundResource(R.drawable.calendar_date);
            holder.dayOfMonth.setTextColor(Color.WHITE);

            holder.dayOfMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), new Date().getDay()+"", Toast.LENGTH_SHORT).show();
                }
            });


        }else {
            holder.dayOfMonth.setText(daysOfMonth.get(position));
        }
    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);
    }
}
