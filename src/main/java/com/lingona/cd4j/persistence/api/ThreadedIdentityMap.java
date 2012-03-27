package CommonDomain.Persistence

import com.lingona.cd4j.api.IAggregate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ThreadedIdentityMap implements IIdentityMap {
    
    private final Map<UUID, Object> locks = new HashMap<UUID, Object>();
    private final Map<UUID, IAggregate> map = new HashMap<UUID, IAggregate>();

    public IAggregate getById(UUID id) {
        IAggregate aggregate = null;
	this.lockAndExecute(id, () => this.map.TryGetValue(id, out aggregate));
	return aggregate;
    }

    public void add(IAggregate aggregate) {
	this.LockAndExecute(aggregate.Id, () => this.map[aggregate.Id] = aggregate);
    }

    public void eject(UUID id) {
	this.LockAndExecute(id, () => this.map.Remove(id));
    }

    private void lockAndExecute(UUID id, Action action) {
	lock (this.ObtainLock(id)) {
	    action();
        }
    }

    private Object obtainLock(UUID id) {
        Object @lock;
	if (this.locks.TryGetValue(id, out @lock)) {
	    return @lock;
        }

	lock (this.locks) {
	    if (!this.locks.TryGetValue(id, out @lock)) {
		this.locks[id] = @lock = new object();
            }
        }
        return @lock;
    }
}