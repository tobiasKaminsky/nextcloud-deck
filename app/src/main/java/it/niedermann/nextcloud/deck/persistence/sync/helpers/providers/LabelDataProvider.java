package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class LabelDataProvider extends AbstractSyncDataProvider<Label> {

    private List<Label> labels;

    public LabelDataProvider(AbstractSyncDataProvider<?> parent, List<Label> labels) {
        super(parent);
        this.labels = labels;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Label>> responder, Date lastSync) {
        responder.onResponse(labels);
    }

    @Override
    public Label getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        return dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        return dataBaseAdapter.createLabel(accountId, entity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        dataBaseAdapter.updateLabel(entity, false);
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Label> responder, Label entity) {
        // TODO: implement
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label label) {
        // TODO: implement
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Label> callback, Label entity) {
        // TODO: implement
    }

    @Override
    public List<Label> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return null;
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, Label entity, Label response) {
        // TODO: implement
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Label> callback, Label entity) {
        // TODO: implement
    }
}
