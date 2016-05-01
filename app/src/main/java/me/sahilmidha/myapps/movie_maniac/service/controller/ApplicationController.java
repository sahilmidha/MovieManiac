package me.sahilmidha.myapps.movie_maniac.service.controller;

/**
 * Created by sahilmidha on 01/05/16.
 *
 * We will access our service layer through this Singleton class.
 */
public class ApplicationController
{
    private static ApplicationController _applicationController;
    private WebServiceManager _webServiceManager;
    private boolean _isInitied = false;

    private ApplicationController()
    {
        if (_applicationController != null)
        {
            throw new IllegalStateException("No two instances of this class can Co-Exist.");
        }
    }

    public static ApplicationController getInstance()
    {
        if (_applicationController == null)
        {
            synchronized (ApplicationController.class)
            {
                _applicationController = new ApplicationController();
            }
        }
        return _applicationController;
    }

    public void init()
    {
        if (!_isInitied)
        {
            _webServiceManager = new WebServiceManager();
            _isInitied = true;
        }
    }

    public WebServiceManager getWebServiceManager()
    {
        return _webServiceManager;
    }
}
