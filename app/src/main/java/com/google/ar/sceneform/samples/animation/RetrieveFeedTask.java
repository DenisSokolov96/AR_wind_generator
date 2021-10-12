package com.google.ar.sceneform.samples.animation;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.AccountControl;
import ru.ctvt.cps.sdk.model.KeyValueStorage;
import ru.ctvt.cps.sdk.model.User;
import ru.ctvt.cps.sdk.model.UserDevice;

public class RetrieveFeedTask extends AsyncTask<String, Void, String> {

    private AccountControl acc = null;
    private User user = null;

    @Override
    protected String doInBackground(String... parameter) {
        String Str_one = "";
        for (int i = 0; i < parameter.length; i++)
            Str_one += parameter[i];

        try {
            Set_Data_Speed(Str_one);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BaseCpsException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // [... Сообщите о результате через обновление пользовательского интерфейса, диалоговое окно или уведомление ...]
        super.onPostExecute(result);
    }

    private void Set_Data_Speed(String speed)throws IOException, BaseCpsException {

        if (user==null) authorization();
        else user = acc.restoreUser();

        //получаю список устройств//getDevices только у Device, у User fetchDevices
        List<UserDevice> devices = user.fetchDevices();
        //получаю номер устройства и создаю
        UserDevice user_device = null;

        user_device = devices.get(1);

        KeyValueStorage storageLocal = user_device.getLocalKVStorage();
        storageLocal.putValue("for_esp", "PWM", speed);

        acc.logout();
    }

    private void authorization()throws IOException, BaseCpsException {
        acc = AccountControl.getInstance().withRole(AccountControl.Role.user);

        String email = "";//логин аккаунта
        String password = "";//пароль аккаунта
        String serviceId = "";//сервис
        //авторизация
        user = acc.login(email, password, serviceId);

    }

}