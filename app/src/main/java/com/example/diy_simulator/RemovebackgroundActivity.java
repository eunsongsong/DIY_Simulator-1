package com.example.diy_simulator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class RemovebackgroundActivity extends AppCompatActivity {

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removebackground);

        //AsyncTask 작동시킴(파싱)
        new Description().execute();
    }

    private class Description extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            try{
                Document doc = Jsoup.connect("https://www.remove.bg/").get();
                Elements elements = doc.select("body").first().children();
                //Elements elements = doc.select("p");//or only `<p>` elements
                for (Element el : elements)
                  Log.d("어디보자","element: "+el);
                //Log.d("어디보자",doc.toString());
                //필요한 녀석만 꼬집어서 지정

                Elements mElementDataSize = doc.select("ul[class=lst_detail_t1]").select("li");
                //목록이 몇개인지 알아낸다. 그만큼 루프를 돌려야 하나깐.

                int mElementSize = mElementDataSize.size();

                for(Element elem : mElementDataSize) { //이렇게 요긴한 기능이
                    //영화목록 <li> 에서 다시 원하는 데이터를 추출해 낸다.
                    String my_title = elem.select("li dt[class=tit] a").text();
                    String my_link = elem.select("li div[class=thumb] a").attr("href");
                    String my_imgUrl = elem.select("li div[class=thumb] a img").attr("src");
                    //특정하기 힘들다... 저 앞에 있는집의 오른쪽으로 두번째집의 건너집이 바로 우리집이야 하는 식이다.
                    Element rElem = elem.select("dl[class=info_txt1] dt").next().first();
                    String my_release = rElem.select("dd").text();
                    Element dElem = elem.select("dt[class=tit_t2]").next().first();
                    String my_director = "감독: " + dElem.select("a").text();

                    // list.add(new ItemObject(my_title, my_imgUrl, my_link, my_release, my_director));

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    */

}