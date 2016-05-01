package me.sahilmidha.myapps.movie_maniac.service.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

import me.sahilmidha.myapps.movie_maniac.service.iWebServiceResponseListener;
import me.sahilmidha.myapps.movie_maniac.service.processor.iDataProcessor;
import me.sahilmidha.myapps.movie_maniac.utils.URLHolder;

/**
 * Created by sahilmidha on 01/05/16.
 */
public class DataFetcher
{
    private iDataProcessor _iDataProcessor;
    private WeakReference<iWebServiceResponseListener> _iWebServiceResponseListener;

    protected final static int REQUEST_READ_TIME_OUT = 10000;
    protected final static int REQUEST_CONNECTION_TIME_OUT = 15000;

    public void fetchData(URLHolder request, iDataProcessor processor, iWebServiceResponseListener listener)
    {
        _iDataProcessor = processor;
        _iWebServiceResponseListener = new WeakReference<iWebServiceResponseListener>(listener);

        new DataFetcherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, request);
    }

    private class DataFetcherTask extends AsyncTask<URLHolder, Object, Boolean>
    {
        @Override
        protected Boolean doInBackground(URLHolder... params)
        {
            String response = executeNetworkRequest(params[0]);
            return _iDataProcessor.parseData(response);
        }

        private String executeNetworkRequest(URLHolder requestBuilder)
        {
            java.net.URL url;
            HttpURLConnection connection = null;
            try
            {
                url = new java.net.URL(requestBuilder.getRequestUrl());


                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(REQUEST_READ_TIME_OUT);
                connection.setConnectTimeout(REQUEST_CONNECTION_TIME_OUT);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //connection.setRequestProperty("Accept", "application/json");

                connection.setUseCaches(false);
                //connection.setDoInput(false);
                //connection.setDoOutput(true);
                Log.v("DataFetcher", url.toString());
                int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK)
                {
                    throw new Exception();
                }

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null)
                {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return null;// we can create custom classes to handle multiple type of scenerios.
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if (result)
            {
                if (_iWebServiceResponseListener != null)
                {
                    _iWebServiceResponseListener.get().onWebServiceSuccess(_iDataProcessor);
                }
            }
            else
            {
                if (_iWebServiceResponseListener != null)
                {
                    _iWebServiceResponseListener.get().onWebServiceFailed();
                }
            }
        }
    }
}
