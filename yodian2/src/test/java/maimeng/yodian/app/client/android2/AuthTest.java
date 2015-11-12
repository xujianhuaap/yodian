package maimeng.yodian.app.client.android2;

import org.junit.Test;

import java.io.IOException;

import maimeng.yodian.app.client.android2.model.Auth;
import maimeng.yodian.app.client.android2.network.response.AuthResponse;
import maimeng.yodian.app.client.android2.network.service.AuthService;
import maimeng.yodian.app.client.android2.network.Network;
import retrofit.Call;
import retrofit.Response;

public class AuthTest extends AbstractTest {
    private AuthService service;

    @Test
    public void login() throws IOException {
        maimeng.yodian.app.client.android2.network.response.Response codeRes= service.getCode("18516668150").execute().body();
        json(codeRes);
        assert codeRes.getCode()==20000;
        Call<AuthResponse> call = service.login("18516668150", "1000", "asdfasfsf");
        Response<AuthResponse> response = call.execute();
        AuthResponse body = response.body();
        Auth data = body.getData();
        assert data !=null;
        json(body);
        Auth user = data;


    }

    @Override
    protected void before() {
        service = Network.getService(AuthService.class);
    }
}
