package com.multisoftware.android.maps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private LatLng address;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        String polyline = "|w`gCdjm{Gc@pAO^QVc@h@]Xq@zAa@dAc@xAk@nDa@tBh@N`Bf@p@Zl@\\pEzEQ|@Y~AId@r@t@dD`D~DhEtBzCbAfAt@`@fA^xLbD`Bp@|AbA|AnA~ApBfB|B`EnEfBjCtBfD^t@TbAFjAKjC{@bOUdEIvAKnESte@OpBi@tAo@v@w@f@_DvAs@d@c@d@Un@m@zCS|A@`B`@|B|@~CpFvMVl@Fr@GdAa@rAyC`HkA|C[dC@pCVlBfJ`_@ZzAZjBnA~Lh@rBp@vAbC`EbD`FhDrFhAdCl@hBn@pCvAnGtBpJxF`QNd@lC|ITt@dDhK|@xEtAhHrBvMd@tAn@|@r@j@z@d@|@TrAXv@Tz@d@p@x@^nAj@pCpAfGfEnS`@z@p@h@j@Pn@FrDFtAJ|@Xx@d@hBbBvG|Gp@x@`@~@PbADjAAlDLvCJlBhBzP@DVlCTpB@Fl@xFDd@VtBh@lB`ArAfAx@fA\\dGbAlCb@zAXJBXDd@HhPhCr@JvATvDp@jJ~A`AV|@^nAp@x@l@n@p@l@v@`AvAz@dBfA~Bd@zAb@hB~@`ErMlj@`@jBXfBPlBR`GNrHFxALxATvA\\tAd@rAj@fAt@hA|@`ArAhAbT|ObBpALHlB|An@p@j@t@d@v@d@dAZ|@lItYn@nBr@jB`BbDfMtUp@~@t@t@z@n@bAj@~@\\|@ThAPjAL`BDfDCtUc@rLOv]g@vR]hBAzADpALhALzB`@`LxBnHrAlHxApEz@rJjB`IdB~Ab@xAd@|Bz@xB~@jIdDhXnKNFhAd@v@\\^Nv@XlMbFtAh@pAn@fAr@p@l@n@t@p@|@^v@^z@\\fA`@~Al@xCtM|q@dBxITpA@DRjA@@Lp@BLHb@fBjJ\\bB~@`Fl@xCjDrQzBtL~DrS`DpPbCjMx@jEh@~Bn@pBh@xAh@rAr@tAp@lAv@hAr@dAx@|@vDxDjDfDxJtJtQjQhGfGrBjBfAt@~@f@`Ab@rBp@pCj@hAJrCXxD^hBNlBF|CRlE^pBThB\\xAb@tAj@dBz@rAbA|AxAbAtAt@lAj@hAf@tA^lAt@bD^vA`@hAj@hAn@~@x@|@r@n@`Al@bAf@l@TlAXhARrAJjABfAEvAMjEq@zB[`Ca@\\El@CbACdA@dAD`AH|@Jj@JhAVbCx@dC~@~`@rOtR~H`C~@FB`DlA`TnIfI|CjAf@hBt@rBt@VJdO|FhCxAb@^n@l@h@v@b@|@t@tBZbAN`ABj@@l@Ar@Kv@q@pBS\\kKnOcCfFuAdEiDpNqBbIm@zBi@vAi@t@o@|@y@r@u@d@_Ah@{@ViAXwARaCXw@Pe@Nm@Zk@b@e@f@e@t@s@tAgDlHsA|C[~@[dAWtAKrAGzA@jBFvAJ|@P`ARfA\\pA\\`AbA`ChArBtJhQfG|Kb@x@Xp@\\pANt@Hp@Dl@@hAC|@IbAO`AU~@c@tAkBnEa@jA[nAWnASxAO~AClA@xAFjAJhAVxAjAxFhCzLpAlGPv@~CrOv@`IXhI?dD_Atv@QlGe@tE_ArEaC`H}Pfd@oBhFo@pCYxCC|CHjB`@tC`@bB`BvDp`@`k@lG|I|@hBp@nBf@hCXnCPdJL~BL`GBlAVxPG`I}AzZsBh\\Cj@_@`H}AdVoB`_@BlCDrA^tCf@lBvAjC~@fAlDfCnMjIvO`K~@l@`W`P`GxD~BnBtDjEb@h@dAjBt@|AbAtBp@vB|@nDh@lDXtCNtE?`DiCvn@I|AsBvc@QdCOpDOxEBtCLlAv@jGx@fCl@pAx@jAbAtAnNfNhCjCxB`DlAfD~@dFT~CC~I@x@LrV@nD?tA@zA?l@KnAQd@e@b@k@Fi@Ge@_@Uk@Aq@Nq@^i@XUr@Kj@KfBCdAClDI^Az@C|AAjEGnRW`R_@tEEzI?|GXhEj@xF`A~a@jIlO~CzHbBzOlFhGrBd@NjJ~Cfd@lO~D|@pF~@~ATxALrk@|CtEl@dHxAvOfDxjBz`@nARP@nCVzBHjAB|AAnEErJEfGA|FLlGb@`w@hHtUxBnEf@hDt@bEnAlAh@lSdKlBv@tCv@pDr@|D^p{@vAnBHpBLzALvBXhB\\hB^`Cl@rInClF|ANDrA^~A\\pAPbCNxBDdAAfBEhTuAlK[vAA~Z_@xSYlk@y@vDG~MC~CDzJT`AA~AEzBE~E_@xEo@lHcB|DsApOcGjDkAtB]rBO~CEvBH`BPzBb@bS~FxEjAfEb@|F@xRgAzSwAf^_Ez@Kz@KhHs@`Jm@|FU`CKvBItGWza@cBvFM~C?|F^|Ex@bBd@fA\\dDjA~KnFjXtMv@^fFlCnBfAzDxBhSdLtD|A|DjAlDl@~CXnKX`B?xO`@xABjOZdIP|CDbDFbhAxBnBDnABvl@nAzR`@zFTfDVhEj@hGrA|LnCnYpGj@NdCj@dPrDfHzAbFfAlG~@zD^vHd@luBjI~BHL@lJ`@fFD~H`@xFJlH`@pCR^DfCPbDn@dBf@~@R~C|A|h@z[\\R\\T~ExC|DzBbAl@tAx@r@XlFfBlEzAfGdA`Fb@jFXhGTvIX~HZld@`BpEPvFf@jQlDzLhCVDvKzBnFlArDp@hEv@|OdC|Fp@tBRvCErC_@fC}@fBgArKyHjDgCj@c@bAk@zAu@tAg@lBm@xBe@bBWzGWdCHjCVxDbA`Bh@rAh@bGfC`Br@fQnHpA\\hAb@lBj@~C`AfAn@r@|@PVPZNd@Jf@Jz@DfAFfAFj@NbATx@^fABJh@jBLpADhACxFDjBE|BMpCmApKU~AmDnS_@zCW|CQbDGhB?rBB`FNhE`@tDX~BZdBP|BJdBTzC`@pFNxCpB~Yd@vGZlErAzQZhEpB~Z`@bFNjB|Bv]dCl^b@`Fh@~EhBlO`DxVbBbNjAhJ\\rClDnYlD~XjB|OvD~YxGfi@j@tEb@bD`Jxt@~C~Vn@`FZ|Bb@rDJdCDdA?r@?n@IrAMlASfBIx@CrADdANt@Pd@LZZd@ZZ^R^NZFv@?x@Kf@Of@Y`@_@Zi@Nc@Js@BaAAqBBeBNqA\\wArAkEdBgDl@oAvBuEtDiHjIaQlAyB~@}AhA}AxA{ArBgB|AcAzCuAdBq@zBq@jAYjASpGi@tq@iEvFWtDArGJrGb@~ATx@JdC\\bIjB~R`HvAf@j@R~QrGfHjC`Ct@~IvBhGz@dDP~CJzd@FzKCdCGd@CrAG~Gs@xU}ElTqEdD{@`FgBdEaB|@_@t@]dDsBfCaBfGaErB_AbC{@dFeBpMeClu@cN|Fm@|Fg@lI_@zQElj@[fT?`DIjAGjAOlAMdDq@fBg@jAa@fFeCvG{EdNsKdVwQzGkErNqHr[uOtB_A~Bw@|Be@~Ca@fDSbFG|n@oA`Fe@jB_@pBg@dBs@nBkAzAkAxAcB|KwQxDqGxA{BnA_B~CeDzB_C|BoBpLqJh@a@nEiDtEsDtDwCxGsFtCuCjD}D`E}FhBmCxC{ErAsBzFiJzCeFrHcLlFsHtEwFlAkAfIqHdQyOjGeFtH_EVKnRcIz[}LrYkKzFsCfAk@fDsCdCoCfCwD`BaDrDuI^}@xG_OnEwGzQyVfQ}TpOeQzGoHbQkPfFkE~AkBxBqDhEcG`AiAfIwJxf@ai@x@_A|BeChEmEvEyC|SqJj^yPtNwHp[sUjCoBf@[p@c@bAo@pEyBjHqBjQiEtGeBdJyD~QuNn@g@lLcJ|RkO\\YvMiK`KcIdFyEpEkH~IqSbDoGjD{F~M}P~Z_^x@_AbCuClZm^dHuEhCqAjCcA|I{BbTcFdBe@z@YzCuAlC{AdCiBbC_CtBgClCgF~Uqh@lCeFj@gA`DyGnC}EfEkGrE_GlNaM|KoJdB{ApNgMdCgCl@q@l`@ed@hGgHpJsKpA{AhAoAvCaCxAkAtBgB~@q@n@[j@SVGZEl@Dt@Ff@Hb@LZRj@d@Zj@Pf@Jf@Hp@LpAHtANpCTzCN~AJdAVzAZ|A\\hARv@\\jAb@zAVr@^|@rA|CnFlKjJ|QTb@rE~I`J`RrC`Hr@vB`@lAb@dAb@lAl@vB`@hB`@zAdAjEfBfHh@vBj@rB\\pA`AxDXbAXhAXrApCpKx@bDTz@`If[nH|Yp@vClDdNzA`Fx@~Bt@dBbAjB|ApCj@t@hA~AtA`BpBbCtA`BjQ`TfBpBfEzE|@fAtBfCjBpC|A|CvAdEdBbHzC`NbAvD\\lA|AvFhBhErBjDvDtEbS~OtEtDd]rXrFvEJTAPGHWH}ISwIQi@@]BiAPeAJ_ADmBEQA_DK}A?wAF_DXuBRoAVkAVaCt@{EpBKFsJpDMD";


        final List<LatLng> latLngList = new ArrayList<>(PolyUtil.decode(polyline));
//        for (LatLng lat : latLngList) {
//            Log.d("HERE", lat.latitude + ", " + lat.longitude);
//            mMap.addMarker(
//                    new MarkerOptions()
//                            .position(lat)
//                            .title(lat.latitude + ", " + lat.longitude)
//            );
//        }

        mMap.addPolyline(
                new PolylineOptions()
                        .color(Color.parseColor("#56c0f5"))
                        .clickable(true)
                        .addAll(latLngList)
        );

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(latLngList.get(0))
                        .include(latLngList.get(latLngList.size() - 1))
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
            }
        });


//        boundsBuilder.include(latLngList.get(latLngList.size()-1));
//        boundsBuilder.include(latLngList.get(0));


        //TODO to review (navigation test)
//        Bitmap bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//        bmp.eraseColor(0xffffff00);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(createCustomMarker(24));

//        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_base_marker);
        //BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

        //address = getLocationFromAddress(this, "Rua José de Alencar, 94, chapecó sc");
        LatLng address = new LatLng(-27.0890975,-52.6243636);
        Marker marker = mMap.addMarker(
                new MarkerOptions()
                        .position(address)
                        .snippet("(lat:" + address.latitude + ", lng:" + address.longitude + ")")
                        .title("Minha Fucking Casa")
                        .icon(icon)
        );
        marker.showInfoWindow();
        mMap.setOnInfoWindowClickListener((GoogleMap.OnInfoWindowClickListener) this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 15));

//        bmp.recycle();


    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();

        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + address.latitude + "," + address.longitude));
        startActivity(navigation);
        //finishAndRemoveTask();
    }


    public Bitmap createCustomMarker(int order) {

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_base_marker);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(icon.getWidth(), icon.getHeight(), conf);
        Canvas canvas = new Canvas(bmp);

        Paint color = new Paint();
        color.setFakeBoldText(true);
        color.setTextAlign(Paint.Align.CENTER);
        color.setColor(0xffffffff);
        int MY_DIP_VALUE = 14; //5dp
        color.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                MY_DIP_VALUE,
                getResources().getDisplayMetrics()
        ));


        Paint iconColor = new Paint();
        iconColor.setColorFilter(new PorterDuffColorFilter(0xffd45b5b, PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(icon, 0, 0, iconColor);
        canvas.drawText(String.valueOf(order), canvas.getWidth() >> 1, (canvas.getHeight() >> 1), color);

        return bmp;
    }

}
