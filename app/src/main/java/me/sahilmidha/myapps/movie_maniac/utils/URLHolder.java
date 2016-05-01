package me.sahilmidha.myapps.movie_maniac.utils;

/**
 * Created by sahilmidha on 01/05/16.
 */
public class URLHolder
{
    //REQUEST_TYPE type, iWebServiceResponseListener listener, List<RequestParams> queryParams, String requestBody)

    String _requestUrl;
    String _requestBody;

    public URLHolder()
    {
    }

    public URLHolder holdURL(String url, String requestBody)
    {
        _requestUrl = url;
        _requestBody = requestBody;
        return this;
    }

    public String getRequestUrl()
    {
        return _requestUrl;
    }

    public String getRequestBody()
    {
        return _requestBody;
    }
}
