package maimeng.yodian.app.client.android2;

import org.junit.Test;

import java.io.IOException;

import maimeng.yodian.app.client.android2.model.Auth;
import maimeng.yodian.app.client.android2.network.response.AuthResponse;
import maimeng.yodian.app.client.android2.network.response.UserInfoResponse;
import maimeng.yodian.app.client.android2.network.service.AuthService;
import maimeng.yodian.app.client.android2.network.Network;
import maimeng.yodian.app.client.android2.network.response.Response;
import maimeng.yodian.app.client.android2.network.service.UserService;
import retrofit.Call;

/**
 * Created by android on 2015/11/12.
 */
public class UserTest extends AbstractTest {
    private UserService service;
    @Test
    public void getInfo() throws IOException {
        Response codeRes= Network.getService(AuthService.class).getCode("18516668150").execute().body();
        json(codeRes);
        assert codeRes.getCode()==20000;
        Call<AuthResponse> call = Network.getService(AuthService.class).login("18516668150", "1000", "asdfasfsf");
        retrofit.Response<AuthResponse> response = call.execute();
        AuthResponse body = response.body();
        Auth data = body.getData();
        assert data !=null;
        json(body);
        Auth user = data;
        YApplication.user=user;
        retrofit.Response<UserInfoResponse> execute = service.getInfo(user.getUid()).execute();
        assert execute.body()!=null;
        json(execute.body());
    }

    @Override
    protected void before() {
        service=Network.getService(UserService.class);

    }
}
