package apps.dcc.com.dclearning;

import android.content.Context;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListItemAdaptar extends ArrayAdapter<ListItem> {

    public ListItemAdaptar(Context context, List<ListItem> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_course, parent, false);
        }

        TweetViewHolder viewHolder = (TweetViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new TweetViewHolder();
            viewHolder.pseudo = (TextView) convertView.findViewById(R.id.course_name);
            viewHolder.text = (TextView) convertView.findViewById(R.id.course_descript);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.cap_course);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        ListItem tweet = getItem(position);
        viewHolder.pseudo.setText(tweet.getPseudo());
        viewHolder.text.setText(tweet.getText());
        viewHolder.avatar.setImageResource(tweet.getColor());

        return convertView;
    }

    private class TweetViewHolder{
        public TextView pseudo;
        public TextView text;
        public ImageView avatar;

    }
}
