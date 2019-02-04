package uncommon.common.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uncommon.common.R;
import uncommon.common.api_interface.ApiInterface;
import uncommon.common.models.Reservation;
import uncommon.common.network.RetrofitInstance;

public class ConfirmReservationActivity extends AppCompatActivity {

    private Button reviewButton;
    private ImageView classImgView;
    private Integer reservationID;
    private TextView classTextView;
    private TextView expertTextView;
    private TextView resDateTextView;
    private TextView usdTextView;
    private TextView resTimeTextView;
    private TextView resUserEmailTextInfo;
    private Integer classID;

    String base_image_url = "http://52.8.187.167:8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reservation);

        Bundle bundle = this.getIntent().getExtras();

        classImgView = (ImageView) this.findViewById(R.id.classImgView);
        classTextView = (TextView) this.findViewById(R.id.classTextView);
        expertTextView = (TextView) this.findViewById(R.id.expertTextView);
        resDateTextView = (TextView) this.findViewById(R.id.resDateTextView);
        resTimeTextView = (TextView) this.findViewById(R.id.resTimeTextView);
        resUserEmailTextInfo = (TextView) this.findViewById(R.id.resUserEmailTextInfo);
        reviewButton = (Button) this.findViewById(R.id.reviewButton);
        usdTextView = (TextView) this.findViewById(R.id.usdTextView);

        // class Img
        reservationID = bundle.getInt("_reservationID");

        ImageView classImgView = (ImageView) findViewById(R.id.classImgView);

        ApiInterface service = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<Reservation> request = service.getReservation(reservationID);
        request.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {

                Reservation res = response.body();

                classTextView.setText(res.getClassName());
                expertTextView.setText(res.getExpertName());
                resTimeTextView.setText(res.getStartTime()+ " - " + res.getEndTime());
                resUserEmailTextInfo.setText(res.getUserEmail());
                usdTextView.setText("$ " + res.getTotalResPrice() * res.getGuestCount());
                Picasso.get()
                        .load(base_image_url + res.getCoverImg())
                        .fit()
                        .into(classImgView);

                //convert date format yyyy-MM-dd into E, MMM dd,  yyyy
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("MMM dd, E, yyyy");
                String inputDateStr= res.getDate();
                Date date = null;
                try {
                    date = inputFormat.parse(inputDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String outputDateStr = outputFormat.format(date);
                resDateTextView.setText(outputDateStr);
                classID = res.getClassId();
                if(!res.getIsPassed()){
                    reviewButton.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                //TODO (kahye)
            }
        });

        //go to ReviewActivity
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(ConfirmReservationActivity.this, ReviewActivity
                        .class);
                Bundle bundle = new Bundle();
                reviewIntent.putExtra("_classID", classID);
                reviewIntent.putExtras(bundle);
                startActivity(reviewIntent);
            }
        });
    }
}