package io.uiza.uiza_sdk_player.v3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.uiza.core.api.UizaV3Service;
import io.uiza.core.models.v3.CreateMetadataBody;
import io.uiza.core.models.v3.DeleteUserBody;
import io.uiza.core.models.v3.IdResponse;
import io.uiza.core.models.v3.ListV3Wrapper;
import io.uiza.core.models.v3.LiveV3Entity;
import io.uiza.core.models.v3.MetadataDetail;
import io.uiza.core.models.v3.ObjectV3Wrapper;
import io.uiza.core.models.v3.RequestUserBody;
import io.uiza.core.models.v3.UpdatePasswordBody;
import io.uiza.core.models.v3.User;
import io.uiza.core.utils.ObservableKt;
import io.uiza.uiza_sdk_player.R;
import io.uiza.uiza_sdk_player.SampleApplication;
import timber.log.Timber;

public class UizaTestAPIV3Activity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private TextView tv;
    private final String entityIdDefaultVOD = "b7297b29-c6c4-4bd6-a74f-b60d0118d275";
    private final String entityIdDefaultLIVE = "45a908f7-a62e-4eaf-8ce2-dc5699f33406";
    private final String metadataDefault0 = "00932b61-1d39-45d2-8c7d-3d99ad9ea95a";
    UizaV3Service v3Service;
    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);
        tv = findViewById(R.id.tv);
        v3Service = ((SampleApplication) getApplication()).getV3Service();
        compositeDisposable = new CompositeDisposable();
        findViewById(R.id.bt_create_an_user).setOnClickListener(this);
        findViewById(R.id.bt_retrieve_an_user).setOnClickListener(this);
        findViewById(R.id.bt_list_all_user).setOnClickListener(this);
        findViewById(R.id.bt_update_an_user).setOnClickListener(this);
        findViewById(R.id.bt_delete_an_user).setOnClickListener(this);
        findViewById(R.id.bt_update_password).setOnClickListener(this);

        findViewById(R.id.bt_get_list_metadata).setOnClickListener(this);
        findViewById(R.id.bt_create_metadata).setOnClickListener(this);
        findViewById(R.id.bt_get_detail_of_metadata).setOnClickListener(this);
        findViewById(R.id.bt_update_metadata).setOnClickListener(this);
        findViewById(R.id.bt_delete_an_metadata).setOnClickListener(this);

        findViewById(R.id.bt_list_all_entity).setOnClickListener(this);
        findViewById(R.id.bt_list_all_entity_metadata).setOnClickListener(this);
        findViewById(R.id.bt_retrieve_an_entity).setOnClickListener(this);
        findViewById(R.id.bt_search_entity).setOnClickListener(this);

        findViewById(R.id.bt_get_token_streaming).setOnClickListener(this);
        findViewById(R.id.bt_get_link_play).setOnClickListener(this);
        findViewById(R.id.bt_retrieve_a_live_event).setOnClickListener(this);
        findViewById(R.id.bt_get_token_streaming_live).setOnClickListener(this);
        findViewById(R.id.bt_get_link_play_live).setOnClickListener(this);
        findViewById(R.id.bt_get_view_a_live_feed).setOnClickListener(this);
        findViewById(R.id.bt_get_time_start_live).setOnClickListener(this);

        findViewById(R.id.bt_list_skin).setOnClickListener(this);
        findViewById(R.id.bt_skin_config).setOnClickListener(this);
        findViewById(R.id.bt_ad).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        tv.setText("Loading...");
        switch (v.getId()) {
            case R.id.bt_create_an_user:
                createAnUser();
                break;
            case R.id.bt_retrieve_an_user:
                retrieveAnUser();
                break;
            case R.id.bt_list_all_user:
                listAllUsers();
                break;
            case R.id.bt_update_an_user:
                updateAnUser();
                break;
            case R.id.bt_delete_an_user:
                deleteAnUser();
                break;
            case R.id.bt_update_password:
                updatePassword();
                break;
            case R.id.bt_get_list_metadata:
                getListMetadata();
                break;
            case R.id.bt_create_metadata:
                createMetadata();
                break;
            case R.id.bt_get_detail_of_metadata:
                getDetailOfMetadata();
                break;
            case R.id.bt_update_metadata:
                updateMetadata();
                break;
            case R.id.bt_delete_an_metadata:
                deleteAnMetadata();
                break;
            case R.id.bt_list_all_entity:
                listAllEntities();
                break;
            case R.id.bt_list_all_entity_metadata:
                listAllEntityMetadata();
                break;
            case R.id.bt_retrieve_an_entity:
                retrieveAnEntity();
                break;
            case R.id.bt_search_entity:
                searchAnEntity();
                break;
            default:
                break;
        }
//        switch (v.getId()) {


//            case R.id.bt_get_token_streaming:
//                getTokenStreaming();
//                break;
//            case R.id.bt_get_link_play:
//                getLinkPlay();
//                break;
//            case R.id.bt_retrieve_a_live_event:
//                retrieveALiveEvent();
//                break;
//            case R.id.bt_get_token_streaming_live:
//                getTokenStreamingLive();
//                break;
//            case R.id.bt_get_link_play_live:
//                getLinkPlayLive();
//                break;
//            case R.id.bt_get_view_a_live_feed:
//                getViewALiveFeed();
//                break;
//            case R.id.bt_get_time_start_live:
//                getTimeStartLive();
//                break;
//            case R.id.bt_list_skin:
//                getListSkin();
//                break;
//            case R.id.bt_skin_config:
//                getSkinConfig();
//                break;
//            case R.id.bt_ad:
//                getIMAAd();
//                break;
//        }
    }

    private void createAnUser() {
        RequestUserBody createUser = new RequestUserBody(null, 1,
                "username " + System.currentTimeMillis(),
                "email " + System.currentTimeMillis(),
                "123456789", "path", "fullname", "11/11/1111", 1, 0);
        Observable<IdResponse> obs = v3Service.createUser(createUser).map(ObjectV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, o -> tv.setText(o.getId()), throwable -> {
            Timber.e(throwable, "createAnUser onFail");
            tv.setText(throwable.getLocalizedMessage());
        }));
    }

    private void retrieveAnUser() {
        Observable<User> obs = v3Service.getUser("6d812978-4253-4bb6-995d-2e6fa82b28e7").map(ObjectV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, user -> tv.setText(user.toString()), throwable -> {
            tv.setText(throwable.getLocalizedMessage());
            Timber.e("createAnUser onFail: %s", throwable.toString());
        }));
    }

    private void listAllUsers() {
        Observable<List<User>> obs = v3Service.getUsers().map(ListV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, users -> tv.setText(User.toJson(users)), throwable -> {
            tv.setText(throwable.getLocalizedMessage());
            Timber.e("createAnUser onFail: %s", throwable.toString());
        }));
    }

    private void updateAnUser() {
        RequestUserBody userBody = new RequestUserBody("6d812978-4253-4bb6-995d-2e6fa82b28e7", 1,
                "username " + System.currentTimeMillis(),
                "email " + System.currentTimeMillis(),
                "123456789",
                "path",
                "fullname",
                "11/11/1111",
                1, 0);
        Observable<User> obs = v3Service.updateUser(userBody).map(ObjectV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, o -> tv.setText(o.toString()), throwable -> {
            Timber.e(throwable, "updateAnUser onFail ");
            tv.setText(throwable.getLocalizedMessage());
        }));
    }

    private void deleteAnUser() {
        Observable<IdResponse> obs = v3Service.deleteUser(new DeleteUserBody("6d812978-4253-4bb6-995d-2e6fa82b28e7")).map(ObjectV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, o -> tv.setText(o.toString()), throwable -> {
            tv.setText(throwable.getLocalizedMessage());
            Timber.e(throwable, "deleteAnUser onFail");
        }));
    }

    private void updatePassword() {
        UpdatePasswordBody updatePassword = new UpdatePasswordBody(
                "6d812978-4253-4bb6-995d-2e6fa82b28e7",
                "oldpassword",
                "newpassword");
        Observable<IdResponse> obs = v3Service.updatePassword(updatePassword).map(ObjectV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, o -> tv.setText(o.toString()), throwable -> {
            tv.setText(throwable.getLocalizedMessage());
            Timber.e(throwable, "updatePassword onFail");
        }));
    }

    private void getListMetadata() {
        Observable<List<MetadataDetail>> obs = v3Service.getMetadatas(10, 0).map(ListV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, o -> tv.setText(MetadataDetail.toJson(o)), throwable -> {
            Timber.e(throwable, "getListMetadata onFail ");
            tv.setText(throwable.getLocalizedMessage());
        }));
    }

    private void createMetadata() {
        CreateMetadataBody createMetadata = new CreateMetadataBody(null,
                "Loitp " + System.currentTimeMillis(),
                CreateMetadataBody.TYPE_FOLDER,
                "This is a description sentences",
                1, "/exemple.com/icon.png");
        Observable<IdResponse> obs = v3Service.createMetadata(createMetadata).map(ObjectV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, o -> tv.setText(o.toString()), throwable -> {
            Timber.e(throwable, "createMetadata onFail");
            tv.setText(throwable.getLocalizedMessage());
        }));
    }

    private void getDetailOfMetadata() {
        Observable<MetadataDetail> obs = v3Service.getDetailOfMetadata("66a4f1d0-ac62-4441-a217-1fa8653bca0a").map(ObjectV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, o -> tv.setText(o.toString()), throwable -> {
            tv.setText(throwable.getLocalizedMessage());
            Timber.e(throwable, "getDetailOfMetadata onFail");
        }));
    }

    private void updateMetadata() {
        CreateMetadataBody createMetadata = new CreateMetadataBody(
                "ce1a4735-99f4-4968-bf2a-3ba8063441f4",
                "@@@Loitp Suzuki GSX S1000",
                CreateMetadataBody.TYPE_PLAYLIST,
                "Update description",
                69,
                "/exemple.com/icon_002.png");
        Observable<MetadataDetail> obs = v3Service.updateMetadata(createMetadata).map(ObjectV3Wrapper::getData);
        compositeDisposable.add(ObservableKt.ioSubscribe(obs, o -> tv.setText(o.toString()), throwable -> {
            tv.setText(throwable.getLocalizedMessage());
            Timber.e(throwable, "updateMetadata onFail");
        }));
    }

    private void deleteAnMetadata() {
        String deleteMetadataId = "37b865b3-cf75-4faa-8507-180a9436d95d";
        compositeDisposable.add(ObservableKt.ioSubscribe(
                v3Service.deleteMetadata(deleteMetadataId)
                        .map(ObjectV3Wrapper::getData),
                o -> tv.setText(o.toString()), throwable -> {
                    tv.setText(throwable.getLocalizedMessage());
                    Timber.e(throwable, "deleteAnMetadata onFail");
                }));
    }


    private void listAllEntities() {
        String metadataId = "";
        int limit = 50;
        int page = 0;
        String orderBy = "createdAt";
        String orderType = "DESC";
        compositeDisposable.add(ObservableKt.ioSubscribe(
                v3Service.getEntities(metadataId, limit, page, orderBy, orderType, "success")
                        .map(ListV3Wrapper::getData), o -> tv.setText(LiveV3Entity.toJson(o))
                , throwable -> {
                    Timber.e(throwable, "listAllEntity onFail ");
                    tv.setText(throwable.getMessage());
                }));
    }

    private void listAllEntityMetadata() {
        String metadataId = "74cac724-968c-4e6d-a6e1-6c2365e41d9d";
        int limit = 50;
        int page = 0;
        String orderBy = "createdAt";
        String orderType = "DESC";
        compositeDisposable.add(ObservableKt.ioSubscribe(
                v3Service.getEntities(metadataId, limit, page, orderBy, orderType, "success")
                        .map(ListV3Wrapper::getData), o -> tv.setText(LiveV3Entity.toJson(o)), throwable -> {
                    tv.setText(throwable.getLocalizedMessage());
                    Timber.e(throwable, "listAllEntityMetadata onFail");
                }));
    }

    private void retrieveAnEntity() {
        String id = "7789b7cc-9fd8-499b-bd35-745d133b6089";
        compositeDisposable.add(ObservableKt.ioSubscribe(
                v3Service.getEntity(id).map(ObjectV3Wrapper::getData), o -> {
                    if (o != null) {
                        tv.setText(o.toString());
                    }
                }, throwable -> {
                    tv.setText(throwable.getLocalizedMessage());
                    Timber.e(throwable, "retrieveAnEntity onFail");
                }));
    }

    private void searchAnEntity() {
        String keyword = "a";
        compositeDisposable.add(ObservableKt.ioSubscribe(
                v3Service.searchEntities(keyword).map(ListV3Wrapper::getData),
                o -> tv.setText(LiveV3Entity.toJson(o)),
                throwable -> {
                    tv.setText(throwable.getLocalizedMessage());
                    Timber.e(throwable, "searchAnEntity onFail");
                }));
    }

//    private void getTokenStreaming() {
//        UZService service = UZRestClient.createService(UZService.class);
//        SendGetTokenStreaming sendGetTokenStreaming = new SendGetTokenStreaming();
//        sendGetTokenStreaming.setAppId(UZData.getInstance().getAppId());
//        sendGetTokenStreaming.setEntityId(entityIdDefaultVOD);
//        sendGetTokenStreaming.setContentType(SendGetTokenStreaming.STREAM);
//        UZAPIMaster.getInstance().subscribe(service.getTokenStreaming(sendGetTokenStreaming), o -> {
//            LLog.d(TAG, "getListAllEntity onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "createAnUser onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private String tokenStreaming;//value received from api getTokenStreaming
//
//    private void getLinkPlay() {
//        if (tokenStreaming == null || tokenStreaming.isEmpty()) {
//            LToast.show(activity, "Token streaming not found, pls call getTokenStreaming before.");
//            return;
//        }
//        UZRestClientGetLinkPlay.addAuthorization(tokenStreaming);
//        UZService service = UZRestClientGetLinkPlay.createService(UZService.class);
//        String appId = UZData.getInstance().getAppId();
//        String typeContent = SendGetTokenStreaming.STREAM;
//        UZAPIMaster.getInstance().subscribe(service.getLinkPlay(appId, entityIdDefaultVOD, typeContent), o -> {
//            LLog.d(TAG, "getListAllEntity onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "createAnUser onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private void retrieveALiveEvent() {
//        UZService service = UZRestClient.createService(UZService.class);
//        int limit = 50;
//        int page = 0;
//        String orderBy = "createdAt";
//        String orderType = "DESC";
//        UZAPIMaster.getInstance().subscribe(service.retrieveALiveEvent(limit, page, orderBy, orderType), o -> {
//            LLog.d(TAG, "getListAllEntity onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "createAnUser onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private String tokenStreamingLive;//value received from api getTokenStreamingLive
//
//    private void getTokenStreamingLive() {
//        UZService service = UZRestClient.createService(UZService.class);
//        SendGetTokenStreaming sendGetTokenStreaming = new SendGetTokenStreaming();
//        sendGetTokenStreaming.setAppId(UZData.getInstance().getAppId());
//        sendGetTokenStreaming.setEntityId(entityIdDefaultLIVE);
//        sendGetTokenStreaming.setContentType(SendGetTokenStreaming.LIVE);
//        UZAPIMaster.getInstance().subscribe(service.getTokenStreaming(sendGetTokenStreaming), o -> {
//            LLog.d(TAG, "getTokenStreaming onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "getTokenStreaming onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private void getLinkPlayLive() {
//        if (tokenStreamingLive == null || tokenStreamingLive.isEmpty()) {
//            LToast.show(activity, "Token streaming not found, pls call getTokenStreamingLive before.");
//            return;
//        }
//        UZRestClientGetLinkPlay.addAuthorization(tokenStreamingLive);
//        UZService service = UZRestClientGetLinkPlay.createService(UZService.class);
//        String appId = UZData.getInstance().getAppId();
//        String streamName = "ffdfdfdfd";
//        UZAPIMaster.getInstance().subscribe(service.getLinkPlayLive(appId, streamName), o -> {
//            LLog.d(TAG, "getLinkPlayLive onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "getLinkPlayLive onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private void getViewALiveFeed() {
//        UZService service = UZRestClient.createService(UZService.class);
//        String id = "8e133d0d-5f67-45e8-8812-44b2ddfd9fe2";
//        UZAPIMaster.getInstance().subscribe(service.getViewALiveFeed(id), o -> {
//            LLog.d(TAG, "getViewALiveFeed onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "getViewALiveFeed onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private void getTimeStartLive() {
//        UZService service = UZRestClient.createService(UZService.class);
//        String entityId = "8e133d0d-5f67-45e8-8812-44b2ddfd9fe2";
//        String feedId = "46fc46f4-8bc0-4d7f-a380-9515d8259af3";
//        UZAPIMaster.getInstance().subscribe(service.getTimeStartLive(entityId, feedId), o -> {
//            LLog.d(TAG, "getTimeStartLive onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "getTimeStartLive onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private void getListSkin() {
//        UZService service = UZRestClient.createService(UZService.class);
//        UZAPIMaster.getInstance().subscribe(service.getListSkin(Constants.PLATFORM_ANDROID), o -> {
//            LLog.d(TAG, "getListSkin onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "getListSkin onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private void getSkinConfig() {
//        UZService service = UZRestClient.createService(UZService.class);
//        UZAPIMaster.getInstance().subscribe(service.getSkinConfig("645cd2a2-9216-4f5d-a73b-37d3e3034798"), o -> {
//            LLog.d(TAG, "getSkinConfig onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "getSkinConfig onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
//
//    private void getIMAAd() {
//        UZService service = UZRestClient.createService(UZService.class);
//        UZAPIMaster.getInstance().subscribe(service.getCuePoint("0e8254fa-afa1-491f-849b-5aa8bc7cce52"), o -> {
//            LLog.d(TAG, "getCuePoint onSuccess: " + LSApplication.getInstance().getGson().toJson(o));
//            showTv(o);
//        }, throwable -> {
//            LLog.e(TAG, "getCuePoint onFail " + throwable.toString());
//            showTv(throwable.getMessage());
//        });
//    }
}
