package bitdev.cloudmemo.utils;

/**
 * Created by triasbrata on 12/24/15.
 */
import android.content.Context;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import bitdev.cloudmemo.R;
import bitdev.cloudmemo.activities.Home;
import bitdev.cloudmemo.activities.ViewNote;
import bitdev.cloudmemo.models.Note;

/**
 * Created by Ravi on 13/05/15.
 */
public class SwipeListAdapter extends RecyclerView.Adapter<SwipeListAdapter.ViewHolder> {
    private List<Note> notes;
    private int rowLayout;
    private Context mContext;

    public SwipeListAdapter(List<Note> countries, int rowLayout, Context context) {
        this.notes = countries;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Note note = notes.get(i);
        viewHolder.noteTitle.setText(note.getTitle());
        viewHolder.noteMessage.setText(note.getMessage());
        viewHolder.noteId.setText(String.valueOf(i));
        viewHolder.view.setOnClickListener(Home.getInstance().setNote(note,i));
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView noteTitle,noteMessage,noteId;
        public View view;
        public Button btn_note;
        public ViewHolder(View itemView) {
            super(itemView);
            noteTitle = (TextView) itemView.findViewById(R.id.note_title);
            noteMessage = (TextView)itemView.findViewById(R.id.note_message);
            noteId = (TextView)itemView.findViewById(R.id.note_id);
            view  = itemView;

        }


    }
}