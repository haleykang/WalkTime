package mydog.haley.com.walktime;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import mydog.haley.com.walktime.domain.WalkTimeVO;

/**
 * Created by Hailey on 2017. 7. 29..
 */

public class WalkTimeAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<WalkTimeVO> mArrayList;
    private Context mContext;
    private int mPosition;

    // 1. 생성자
    public WalkTimeAdapter(ArrayList<WalkTimeVO> arrayList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mArrayList = arrayList;
        this.mContext = context;
    }

    // 2. ArrayList Getter, Setter
    public ArrayList<WalkTimeVO> getArrayList() {
        return mArrayList;
    }

    public void setArrayList(ArrayList<WalkTimeVO> arrayList) {
        this.mArrayList = arrayList;
    }

    // Position Getter, Setter


    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    // 3. 재정의 함수
    // 리스트에서 보여줄 데이터의 갯수
    @Override
    public int getCount() {

        return mArrayList.size();
    }

    // 선택한 아이템(i)의 코드 값을 리턴하도록
    @Override
    public Object getItem(int i) {

        return mArrayList.get(i).getCode();
    }

    // getItem() 메소드가 리턴한 객체의 고유 주소
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        // 리스트 아이템이 새로 추가 될 경우 view 값은 null
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.time_list_item, null);
            // 아이템 리스트의 뷰의 고유주소 저장
            holder.profileImage = (CircleImageView) view.findViewById(R.id.profile_iv);
            holder.todayTv = (TextView) view.findViewById(R.id.today_tv);
            holder.startEndTv = (TextView) view.findViewById(R.id.start_end_tv);
            holder.timeTv = (TextView) view.findViewById(R.id.walk_time_tv);
            holder.deleteTv = (TextView) view.findViewById(R.id.delete_tv);
            // 뷰에 holder값 저장
            view.setTag(holder);
        } else {
            // view가 null이 아니면 holder의 태그 값을 가져와서 저장
            holder = (ViewHolder) view.getTag();
        }

        // WalkTimeVO 객체 - ArrayList의 포지션에 맞는 데이터 가져오기
        final WalkTimeVO vo = mArrayList.get(position);

        // 리스트 아이템 뷰에 테이블에 입력된 값 출력
        // 프로필 이미지는 일단 생략
        holder.todayTv.setText(vo.getReg_date());
        holder.startEndTv.setText(vo.getStart() + " - " + vo.getEnd());
        holder.timeTv.setText(getStrTime(vo.getWalk_time()));

        // ArrayList에서 해당 포지션의 값만 지우기 위해 태그로 position값 전달
        holder.deleteTv.setTag(position);

        // 삭제 버튼 처리
        holder.deleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("WalkTimeAdapter", "OnClick - Delete WalkTime");
                // 선택한 view의 position 값을 전역 변수로 저장
                setmPosition((int) view.getTag());

                if (callback != null) {
                    callback.onDeleteClick(vo);
                }


            }
        });

        return view;
    }

    // 삭제 버튼 처리를 위한 인터페이스

    // 클릭 처리
    public interface onListButtonClick {

        void onDeleteClick(WalkTimeVO vo);

    }

    private onListButtonClick callback = null;

    public void setOnListButtonClick(onListButtonClick callback) {
        this.callback = callback;
    }

    // ViewHolder 클래스
    private class ViewHolder {

        CircleImageView profileImage = null;
        TextView todayTv = null;
        TextView startEndTv = null;
        TextView timeTv = null;
        TextView deleteTv = null;

    }

    // 사용자 정의 함수
    private String getStrTime(long time) {

        int m = (int) time / 60;

        return m + "분";


    }
}

