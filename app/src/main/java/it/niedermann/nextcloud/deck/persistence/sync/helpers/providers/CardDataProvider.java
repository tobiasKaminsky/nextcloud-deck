package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class CardDataProvider extends AbstractSyncDataProvider<FullCard> {

    private Board board;
    private FullStack stack;

    public CardDataProvider(AbstractSyncDataProvider<?> parent, Board board, FullStack stack) {
        super(parent);
        this.board = board;
        this.stack = stack;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullCard>> responder, Date lastSync) {

        List<FullCard> result = new ArrayList<>();
        if (stack.getCards() == null || stack.getCards().isEmpty()){
            responder.onResponse(result);
        }
        for (Card card : stack.getCards()) {
            serverAdapter.getCard(board.getId(), stack.getId(), card.getId(), new IResponseCallback<FullCard>(responder.getAccount()) {
                @Override
                public void onResponse(FullCard response) {
                    result.add(response);
                    if (result.size() == stack.getCards().size()) {
                        responder.onResponse(result);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    responder.onError(throwable);
                }
            });
        }
    }

    @Override
    public FullCard getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        return dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        fixRelations(dataBaseAdapter, accountId, entity);
        return dataBaseAdapter.createCard(accountId, entity.getCard());
    }

    private void fixRelations(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        entity.getCard().setStackId(stack.getLocalId());
        if (entity.getOwner() != null && !entity.getOwner().isEmpty()){
            User user = entity.getOwner().get(0);
            User u = dataBaseAdapter.getUserByUidDirectly(accountId, user.getUid());
            if (u == null){
                dataBaseAdapter.createUser(accountId, user);
            } else {
                user.setLocalId(u.getLocalId());
                dataBaseAdapter.updateUser(accountId, user, false);
            }
            u = dataBaseAdapter.getUserByUidDirectly(accountId, user.getUid());

            user.setLocalId(u.getLocalId());
            entity.getCard().setUserId(u.getLocalId());
        }
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        fixRelations(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateCard(entity.getCard(), false);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullCard existingEntity, FullCard entityFromServer, IResponseCallback<Boolean> callback) {
        List<Label> labels = entityFromServer.getLabels();
        existingEntity.setLabels(labels);
        List<User> assignedUsers = entityFromServer.getAssignedUsers();
        existingEntity.setAssignedUsers(assignedUsers);
        List<Attachment> attachments = entityFromServer.getAttachments();
        existingEntity.setAttachments(attachments);

        if(labels != null && !labels.isEmpty()){
            syncHelper.doSyncFor(new LabelDataProvider(this, labels));
        }
        syncHelper.fixRelations(new CardLabelRelationshipProvider(existingEntity.getCard(), existingEntity.getLabels()));
        if(assignedUsers!= null && !assignedUsers.isEmpty()){
            syncHelper.doSyncFor(new UserDataProvider(this, board, stack, existingEntity, existingEntity.getAssignedUsers()));
        }
        syncHelper.fixRelations(new CardUserRelationshipProvider(existingEntity.getCard(), existingEntity.getAssignedUsers()));
        if(assignedUsers!= null && !attachments.isEmpty()){
            syncHelper.doSyncFor(new AttachmentDataProvider(this, existingEntity, attachments));
        }
//        syncHelper.doSyncFor(new UserDataProvider(board, stack, existingEntity, existingEntity.getOwner()));
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullCard> responder, FullCard entity) {
        //TODO: implement
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullCard> callback, FullCard entity) {
        //TODO: implement
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard fullCard) {
        //TODO: implement
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullCard> callback, FullCard entity) {
        //TODO: implement
    }

    @Override
    public List<FullCard> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return null;
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, FullCard entity, FullCard response) {
        //TODO: implement
    }
}
