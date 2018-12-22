package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.interfaces.RemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.IPersistenceAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.IDatabaseOnlyAdapter;

public class SyncManager {


    private IDatabaseOnlyAdapter dataBaseAdapter;
    private IPersistenceAdapter serverAdapter;
    private Context applicationContext;
    private Activity sourceActivity;

    public SyncManager(Context applicationContext, Activity sourceActivity) {
        this.applicationContext = applicationContext.getApplicationContext();
        this.sourceActivity = sourceActivity;
        dataBaseAdapter = new DataBaseAdapter(this.applicationContext);
        this.serverAdapter = new ServerAdapter(this.applicationContext, sourceActivity);
    }

    private void doAsync(Runnable r) {
        new Thread(r).start();
    }

    public void synchronize(IResponseCallback<Boolean> responseCallback) {
//        final long accountId = responseCallback.getAccount().getId();
//        doAsync(() -> {
//            SharedPreferences lastSyncPref = applicationContext.getSharedPreferences(
//                    applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
//            long lastSync = lastSyncPref.getLong(DeckConsts.LAST_SYNC_KEY, 0L);
//            Date lastSyncDate = new Date(lastSync);
//            Date now = new Date();
//
//            serverAdapter.getBoards(accountId, new IResponseCallback<List<Board>>(responseCallback.getAccount()) {
//                @Override
//                public void onResponse(List<Board> response) {
//                    for (Board b : response) {
//                        Board existingBoard = dataBaseAdapter.getBoard(accountId, b.getId()).getValue();
//                        if (existingBoard == null) {
//                            dataBaseAdapter.createBoard(accountId, b);
//                        } else {
//                            dataBaseAdapter.updateBoard(applyUpdatesFromRemote(existingBoard, b, accountId));
//                        }
//                        synchronizeStacksOf(b, responseCallback);
//                    }
//                    responseCallback.onResponse(true);
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    responseCallback.onError(throwable);
//                }
//            });
//
//            //TODO activate when done dev
////                lastSyncPref.edit().putLong(LAST_SYNC_KEY, now.getTime()).apply();
//        });
    }

//    private void synchronizeStacksOf(final Board board, final IResponseCallback<Boolean> responseCallback) {
//        //sync stacks
//        Account account = responseCallback.getAccount();
//        long accountId = account.getId();
//        final Board syncedBoard = dataBaseAdapter.getBoard(accountId, board.getId()).getValue();
//        serverAdapter.getStacks(accountId, board.getId(), new IResponseCallback<List<Stack>>(account) {
//            @Override
//            public void onResponse(List<Stack> response) {
//                for (Stack stack : response) {
//                    stack.setBoardId(syncedBoard.getLocalId());
//                    Stack existingStack = dataBaseAdapter.getStack(accountId, syncedBoard.getLocalId(), stack.getId());
//                    if (existingStack == null) {
//                        dataBaseAdapter.createStack(accountId, stack);
//                    } else {
//                        dataBaseAdapter.updateStack(applyUpdatesFromRemote(existingStack, stack, accountId));
//                    }
//                    existingStack = dataBaseAdapter.getStack(accountId, syncedBoard.getLocalId(), stack.getId());
//                    dataBaseAdapter.deleteJoinedCardsForStack(existingStack.getLocalId());
//                    synchronizeCardOf(stack, syncedBoard, responseCallback);
//                }
//                //responseCallback.onResponse(true);
//            }
//
//
//            @Override
//            public void onError(Throwable throwable) {
//                responseCallback.onError(throwable);
//            }
//        });
//    }
//
//    private void synchronizeCardOf(final Stack stack, final Board syncedBoard, final IResponseCallback<Boolean> responseCallback) {
//        //sync cards
//        Account account = responseCallback.getAccount();
//        long accountId = account.getId();
//        Stack syncedStack = dataBaseAdapter.getStack(accountId, syncedBoard.getLocalId(), stack.getId()).getValue();
//
//        for (Card c : stack.getCards()) {
//            DeckLog.log("requesting Card: " + c.getTitle());
//            serverAdapter.getCard(accountId, syncedBoard.getId(), syncedStack.getId(), c.getId(), new IResponseCallback<Card>(account) {
//                @Override
//                public void onResponse(Card card) {
//
//                    List<User> assignedUsers = card.getAssignedUsers();
//                    List<Label> labels = card.getLabels();
//                    card.setStack(syncedStack);
//                    card.setStackId(syncedStack.getLocalId());
//                    Card existingCard = dataBaseAdapter.getCard(accountId, card.getId());
//                    if (existingCard == null) {
//                        DeckLog.log("creating Card...");
//                        dataBaseAdapter.createCard(accountId, card);
//                    } else {
//                        DeckLog.log("updating Card...");
//                        dataBaseAdapter.updateCard(applyUpdatesFromRemote(existingCard, card, accountId));
//                    }
//
//                    existingCard = dataBaseAdapter.getCard(accountId, card.getId());
//                    dataBaseAdapter.createJoinStackWithCard(existingCard.getLocalId(), syncedStack.getLocalId());
//                    existingCard.setLabels(new ArrayList<>());
//                    existingCard.setAssignedUsers(new ArrayList<>());
//
//                    ArrayList<User> existingUsers = new ArrayList<>();
//                    dataBaseAdapter.deleteJoinedUsersForCard(existingCard.getLocalId());
//                    for (User user : assignedUsers) {
//                        User existingUser = dataBaseAdapter.getUser(accountId, user.getId());
//                        if (existingUser == null) {
//                            DeckLog.log("creating user: " + user.getUid());
//                            dataBaseAdapter.createUser(accountId, user);
//                            existingUser = dataBaseAdapter.getUser(accountId, user.getId());
//                        } else {
//                            DeckLog.log("updating user: " + user.getUid());
//                            existingUser = applyUpdatesFromRemote(existingUser, user, accountId);
//                            dataBaseAdapter.updateUser(accountId, existingUser);
//                        }
//                        dataBaseAdapter.createJoinCardWithUser(existingUser.getLocalId(), existingCard.getLocalId());
//                        existingUsers.add(existingUser);
//                    }
//                    ArrayList<Label> existingLabels = new ArrayList<>();
//                    dataBaseAdapter.deleteJoinedLabelsForCard(existingCard.getLocalId());
//                    for (Label label : labels) {
//                        Label existingLabel = dataBaseAdapter.getLabel(accountId, label.getId());
//                        if (existingLabel == null) {
//                            DeckLog.log("creating Label: " + label.getTitle());
//                            dataBaseAdapter.createLabel(accountId, label);
//                        } else {
//                            DeckLog.log("updating Label: " + label.getTitle());
//                            existingLabel = applyUpdatesFromRemote(existingLabel, label, accountId);
//                            dataBaseAdapter.updateLabel(accountId, existingLabel);
//                        }
//                        dataBaseAdapter.createJoinCardWithLabel(existingLabel.getLocalId(), existingCard.getLocalId());
//                        existingLabels.add(existingLabel);
//                    }
//
//                    existingCard.setAssignedUsers(existingUsers);
//                    existingCard.setLabels(existingLabels);
//                    dataBaseAdapter.updateCard(existingCard);
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    responseCallback.onError(throwable);
//                }
//            });
//        }
//    }

    private <T> IResponseCallback<T> wrapCallForUi(IResponseCallback<T> responseCallback) {
        Account account = responseCallback.getAccount();
        if (account == null || account.getId() == null) {
            throw new IllegalArgumentException("Bro. Please just give me a damn Account!");
        }
        return new IResponseCallback<T>(responseCallback.getAccount()) {
            @Override
            public void onResponse(T response) {
                sourceActivity.runOnUiThread(() -> {
                    fillAccountIDs(response);
                    responseCallback.onResponse(response);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                responseCallback.onError(throwable);
            }
        };
    }

    private <T extends RemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        if (!localEntity.getId().equals(remoteEntity.getId())
                || !accountId.equals(localEntity.getAccountId())) {
            throw new IllegalArgumentException("IDs of Account or Entity are not matching! WTF are you doin?!");
        }
        remoteEntity.setLastModifiedLocal(remoteEntity.getLastModified()); // not an error! local-modification = remote-mod
        remoteEntity.setLocalId(localEntity.getLocalId());
        return remoteEntity;
    }

    public boolean hasAccounts() {
        return dataBaseAdapter.hasAccounts();
    }

    public LiveData<Account> createAccount(String accoutName) {
        return dataBaseAdapter.createAccount(accoutName);
    }

    public void deleteAccount(long id) {
        dataBaseAdapter.deleteAccount(id);
    }

    public void updateAccount(Account account) {
        dataBaseAdapter.updateAccount(account);
    }

    public LiveData<Account> readAccount(long id) {
        return dataBaseAdapter.readAccount(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    public void getBoards(long accountId, IResponseCallback<LiveData<List<Board>>> responseCallback) {
        dataBaseAdapter.getBoards(accountId, wrapCallForUi(responseCallback));
    }

    public void createBoard(long accountId, Board board) {
        doAsync(() -> {
            dataBaseAdapter.createBoard(accountId, board);
            serverAdapter.createBoard(accountId, board);
        });
    }

    public void deleteBoard(Board board) {

    }

    public void updateBoard(Board board) {

    }

    public void getStacks(long accountId, long localBoardId, IResponseCallback<LiveData<List<Stack>>> responseCallback) {
        dataBaseAdapter.getStacks(accountId, localBoardId, wrapCallForUi(responseCallback));
    }

    public void getStack(long accountId, long localBoardId, long stackId, IResponseCallback<LiveData<Stack>> responseCallback) {
        dataBaseAdapter.getStack(accountId, localBoardId, stackId, wrapCallForUi(responseCallback));
    }

    public void createStack(long accountId, Stack stack) {
        dataBaseAdapter.createStack(accountId, stack);
        //TODO implement
    }

    public void deleteStack(Stack stack) {

    }

    public void updateStack(Stack stack) {

    }

    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<LiveData<FullCard>> responseCallback) {
        dataBaseAdapter.getCard(accountId, boardId, stackId, cardId, wrapCallForUi(responseCallback));
    }

    public void createCard(long accountId, Card card) {

    }

    public void deleteCard(Card card) {

    }

    public void updateCard(Card card) {

    }
}
