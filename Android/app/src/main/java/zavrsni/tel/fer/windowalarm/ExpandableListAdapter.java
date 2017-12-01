package zavrsni.tel.fer.windowalarm;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Lovro on 13.2.2017..
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerData; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Windows>> itemData;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<Windows>> listChildData) {
        this.context = context;
        this.headerData = listDataHeader;
        this.itemData = listChildData;

    }

    @Override
    public Windows getChild(int groupPosition, int childPosititon) {
        return this.itemData.get(this.headerData.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition).getName();
        final boolean state = (boolean) getChild(groupPosition, childPosition).isState();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        TextView txtState = (TextView) convertView.findViewById(R.id.stateText);
        if(state==true){
            txtState.setText("OPENED");
        } else{
            txtState.setText("CLOSED");
        }
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.itemData.get(this.headerData.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerData.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headerData.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView listHeader = (TextView) convertView
                .findViewById(R.id.listHeader);
        listHeader.setTypeface(null, Typeface.BOLD);
        listHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
