package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import it.niedermann.nextcloud.deck.DeckConsts;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.ApiProvider;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.RequestHelper;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.util.DateUtil;

public class ServerAdapter {

    private static final DateFormat API_FORMAT = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss z");

    static {
        API_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private Context applicationContext;
    private ApiProvider provider;
    private Activity sourceActivity;
    private SharedPreferences lastSyncPref;

    public ServerAdapter(Context applicationContext, Activity sourceActivity) {
        this.applicationContext = applicationContext;
        this.sourceActivity = sourceActivity;
        provider = new ApiProvider(applicationContext);
        lastSyncPref = applicationContext.getSharedPreferences(
                applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
    }

    public String getServerUrl() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        return provider.getServerUrl();
    }

    public String getApiPath() {
        return provider.getApiPath();
    }

    public String getApiUrl() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        return provider.getApiUrl();
    }

    public void ensureInternetConnection() {
        boolean isConnected = hasInternetConnection();
        if (!isConnected){
            throw new OfflineException();
        }
    }

    public boolean hasInternetConnection(){
        ConnectivityManager cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo().isConnected();
    }

    private String getLastSyncDateFormatted() {
//        return null;
        String lastSyncHeader = API_FORMAT.format(getLastSync());
        // omit Offset of timezone (e.g.: +01:00)
        if (lastSyncHeader.matches("^.*\\+[0-9]{2}:[0-9]{2}$")) {
            lastSyncHeader = lastSyncHeader.substring(0, lastSyncHeader.length()-6);
        }
        Log.d("deck lastSync", lastSyncHeader);
        return lastSyncHeader;
    }

    private Date getLastSync() {
//        return new Date(0l);
        //return null;
        // FIXME: reactivate, when lastSync is working in REST-API
        Date lastSync = DateUtil.nowInGMT();
        lastSync.setTime(lastSyncPref.getLong(DeckConsts.LAST_SYNC_KEY, 0L));

        return lastSync;
    }

    public void getBoards(IResponseCallback<List<FullBoard>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getBoards(getLastSyncDateFormatted()), responseCallback);
    }

    public void createBoard(Board board, IResponseCallback<FullBoard> responseCallback) {
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().createBoard(board), responseCallback);
    }

    public void deleteBoard(Board board) {
        ensureInternetConnection();

    }

    public void updateBoard(Board board) {
        ensureInternetConnection();

    }

    public void getStacks(long boardId, IResponseCallback<List<FullStack>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getStacks(boardId, getLastSyncDateFormatted()), responseCallback);
    }

    public void getStack(long boardId, long stackId, IResponseCallback<FullStack> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getStack(boardId, stackId, getLastSyncDateFormatted()), responseCallback);
    }

    public void createStack(Stack stack) {
        ensureInternetConnection();

    }

    public void deleteStack(Stack stack) {
        ensureInternetConnection();

    }

    public void updateStack(Stack stack) {
        ensureInternetConnection();

    }

    public void getCard(long boardId, long stackId, long cardId, IResponseCallback<FullCard> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().getCard(boardId, stackId, cardId, getLastSyncDateFormatted()), responseCallback);
    }

    public void createCard(Card card) {
        ensureInternetConnection();

    }

    public void deleteCard(Card card) {
        ensureInternetConnection();

    }

    public void updateCard(Card card) {
        ensureInternetConnection();

    }

    public void assignUserToCard(long boardId, long stackId, long cardId, String userUID, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().assignUserToCard(boardId, stackId, cardId, userUID), responseCallback);
    }

    public void unassignUserFromCard(long boardId, long stackId, long cardId, String userUID, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().unassignUserFromCard(boardId, stackId, cardId, userUID), responseCallback);
    }

    public void assignLabelToCard(long boardId, long stackId, long cardId, long labelId, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().assignLabelToCard(boardId, stackId, cardId, labelId), responseCallback);
    }

    public void unassignLabelFromCard(long boardId, long stackId, long cardId, long labelId, IResponseCallback<Void> responseCallback){
        ensureInternetConnection();
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().unassignLabelFromCard(boardId, stackId, cardId, labelId), responseCallback);
    }
}
