package de.dfki.lt.hfc.db;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingClients {
  protected static Logger logger = LoggerFactory.getLogger(StreamingClients.class);

  private static class StreamingThread extends Thread {
    private boolean _isRunning = true;
    private final HashSet<String> _copy = new HashSet<>();
    private Semaphore _runnable = new Semaphore(0);
    private StreamingClient _client;
    private Set<String> _affectedUsers = new HashSet<>();

    public StreamingThread(StreamingClient cl) {
      _client = cl;
    }

    public void startComputation(Set<String> affectedUsers) {
      synchronized(_affectedUsers) {
        this._affectedUsers.addAll(affectedUsers);
      }
      if (_runnable.availablePermits() == 0 && _copy.isEmpty())
        _runnable.release();
    }

    public void terminate() {
      _isRunning = false;
      _runnable.release();
    }

    private void copyUsers() {
      _copy.clear();
      synchronized(_affectedUsers) {
        _copy.addAll(_affectedUsers);
        _affectedUsers.clear();
      }
    }

    @Override
    public void run() {
      while (_isRunning) {
        try {
          _runnable.acquire();
          if (_isRunning) {
            copyUsers();
            while (!_copy.isEmpty()) {
              _client.compute(_copy);
              copyUsers();
            }
          }
        }
        catch (InterruptedException ex) {
          _isRunning = false;
        }
      }
    }

    @Override
    public String toString() { return _client.toString(); }
  }

  /** The number of milliseconds before a new change notification is issued.
   *  All changes that occur after a notification during this interval will
   *  be registered and result in a new notification after the interval is
   *  exceeded.
   */
  public static long NOTIFICATION_INTERVAL = 400;

  private final Timer _notificationTimer;

  private long _lastNotification = -1;

  private int _notificationScheduled = 0;

  /** If the system supports multiple users, filtering according to the users
   *  that are affected by a change leads to significant performance improvements
   */
  private final Set<String> _affectedUsers;

  /** To allow for dependent functionality to be informed about changes in the
   *  database
   */
  protected IdentityHashMap<StreamingClient, StreamingThread> _streamingThreads;

  public StreamingClients() {
    _streamingThreads = new IdentityHashMap<>();
    _notificationTimer = new Timer(true);
    _affectedUsers = new HashSet<>();
  }

  public synchronized void shutdown(){
    for (StreamingClient c : _streamingThreads.keySet()) {
      unregisterStreamingClient(c);
    }
  }

  // **********************************************************************
  // Streaming Clients
  // **********************************************************************

  public <T> void registerStreamingClient(StreamingClient client) {
    // TODO watch out for this cast! A cleaner solution is required.
    //client.init(computeClient);
    StreamingThread t = new StreamingThread(client);
    synchronized(_streamingThreads) {
      _streamingThreads.put(client, t);
    }
    t.start();
  }


  public void unregisterStreamingClient(StreamingClient client) {
    synchronized(_streamingThreads) {
      StreamingThread t =_streamingThreads.get(client);
      t.terminate();
      _streamingThreads.put(client, null);
    }
  }

  /** Run the streaming clients, really. */
  private void notifyClients() {
    logger.info("dbChange[{} {}]", _notificationScheduled, _affectedUsers);
    Set<String> users = new HashSet<>();
    synchronized(_affectedUsers) {
      users.addAll(_affectedUsers);
      _affectedUsers.clear();
    }
    _notificationScheduled = 0;
    // retrieve all tuples changed after _lastNotification, run the
    // "which user affected" method and pass the result to the startComputation
    // calls

    _lastNotification = System.currentTimeMillis();
    synchronized(_streamingThreads) {
      Iterator<Map.Entry<StreamingClient, StreamingThread>> it =
          _streamingThreads.entrySet().iterator();
      while(it.hasNext()) {
        StreamingThread t = it.next().getValue();
        if (t != null) {
          t.startComputation(users);
        } else {
          it.remove();
        }
      }
    }
  }

  /** Run the streaming clients, making sure they are only called if the
   *  last change occured at least NOTIFICATION_INTERVAL msecs before.
   *  Otherwise, start a timer that ends NOTIFICATION_INTERVAL msecs after the
   *  last change.
   */
  public void startStreamingClients(Set<String> affectedUsers) {
    if (affectedUsers.isEmpty()) return;
    synchronized(_affectedUsers) {
      _affectedUsers.addAll(affectedUsers);
    }
    long now = System.currentTimeMillis();
    // is a notification timer running?
    if (_notificationScheduled == 0) {
      // is this change within the silencing interval?
      long delta = now - _lastNotification;
      if (delta < NOTIFICATION_INTERVAL) {
        // yes: start a new notification timer
        _notificationScheduled = 1;
        _notificationTimer.schedule(new TimerTask() {
          @Override
          public void run() { notifyClients(); }
        }, NOTIFICATION_INTERVAL - delta);
      } else {
        notifyClients();
      }
    } else {
      ++_notificationScheduled;
    }
  }
}
