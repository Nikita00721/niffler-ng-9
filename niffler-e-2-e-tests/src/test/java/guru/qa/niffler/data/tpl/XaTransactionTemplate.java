package guru.qa.niffler.data.tpl;

import com.atomikos.icatch.jta.UserTransactionImp;
import jakarta.transaction.UserTransaction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class XaTransactionTemplate {

    private final JdbcConnectionHolders holders;
    private final AtomicBoolean closeAfterAction = new AtomicBoolean(true);

    public XaTransactionTemplate(String... jdbcUrl) {
        this.holders = Connections.holders(jdbcUrl);
    }

    public XaTransactionTemplate holdConnectionAfterAction() {
        this.closeAfterAction.set(false);
        return this;
    }

    public <T> T execute(Supplier<T>... actions) throws Exception {
        UserTransaction ut = new UserTransactionImp();
        try {
            ut.begin();
            T result = null;
            for (Supplier<T> action : actions) {
                result = action.get();
            }
            ut.commit();
            return result;
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (Exception rollbackException) {
                throw new RuntimeException("Rollback failed", rollbackException);
            }
            throw new RuntimeException("XA Transaction failed", e);
        } finally {
            if (closeAfterAction.get()) {
                holders.close();
            }
        }
    }
}


