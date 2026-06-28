package com.money.app.data;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TransactionDao _transactionDao;

  private volatile FundDao _fundDao;

  private volatile ChatMessageDao _chatMessageDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(15, "6d0a527241a8c45a7aad5597438d20a0", "ab461fbb1e866f033f3e5a2c83a33108") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `syncId` TEXT NOT NULL, `userId` TEXT NOT NULL, `amount` REAL NOT NULL, `category` TEXT NOT NULL, `date` TEXT NOT NULL, `description` TEXT NOT NULL, `imagePath` TEXT, `isExpense` INTEGER NOT NULL, `rating` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_transactions_syncId` ON `transactions` (`syncId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `funds` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `syncId` TEXT NOT NULL, `ownerId` TEXT NOT NULL, `name` TEXT NOT NULL, `currentAmount` REAL NOT NULL, `targetAmount` REAL NOT NULL, `icon` TEXT NOT NULL, `createdDate` INTEGER NOT NULL, `endDate` INTEGER NOT NULL, `isPinned` INTEGER NOT NULL, `isShared` INTEGER NOT NULL, `members` TEXT NOT NULL, `memberContributions` TEXT NOT NULL)");
        SQLite.execSQL(connection, "CREATE UNIQUE INDEX IF NOT EXISTS `index_funds_syncId` ON `funds` (`syncId`)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `text` TEXT NOT NULL, `isUser` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6d0a527241a8c45a7aad5597438d20a0')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `transactions`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `funds`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `chat_messages`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(11);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("syncId", new TableInfo.Column("syncId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("userId", new TableInfo.Column("userId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amount", new TableInfo.Column("amount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("imagePath", new TableInfo.Column("imagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("isExpense", new TableInfo.Column("isExpense", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("rating", new TableInfo.Column("rating", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(1);
        _indicesTransactions.add(new TableInfo.Index("index_transactions_syncId", true, Arrays.asList("syncId"), Arrays.asList("ASC")));
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(connection, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenDelegate.ValidationResult(false, "transactions(com.money.app.data.Transaction).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final Map<String, TableInfo.Column> _columnsFunds = new HashMap<String, TableInfo.Column>(13);
        _columnsFunds.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("syncId", new TableInfo.Column("syncId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("ownerId", new TableInfo.Column("ownerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("currentAmount", new TableInfo.Column("currentAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("targetAmount", new TableInfo.Column("targetAmount", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("icon", new TableInfo.Column("icon", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("createdDate", new TableInfo.Column("createdDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("endDate", new TableInfo.Column("endDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("isPinned", new TableInfo.Column("isPinned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("isShared", new TableInfo.Column("isShared", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("members", new TableInfo.Column("members", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFunds.put("memberContributions", new TableInfo.Column("memberContributions", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysFunds = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesFunds = new HashSet<TableInfo.Index>(1);
        _indicesFunds.add(new TableInfo.Index("index_funds_syncId", true, Arrays.asList("syncId"), Arrays.asList("ASC")));
        final TableInfo _infoFunds = new TableInfo("funds", _columnsFunds, _foreignKeysFunds, _indicesFunds);
        final TableInfo _existingFunds = TableInfo.read(connection, "funds");
        if (!_infoFunds.equals(_existingFunds)) {
          return new RoomOpenDelegate.ValidationResult(false, "funds(com.money.app.data.Fund).\n"
                  + " Expected:\n" + _infoFunds + "\n"
                  + " Found:\n" + _existingFunds);
        }
        final Map<String, TableInfo.Column> _columnsChatMessages = new HashMap<String, TableInfo.Column>(4);
        _columnsChatMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("isUser", new TableInfo.Column("isUser", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysChatMessages = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesChatMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChatMessages = new TableInfo("chat_messages", _columnsChatMessages, _foreignKeysChatMessages, _indicesChatMessages);
        final TableInfo _existingChatMessages = TableInfo.read(connection, "chat_messages");
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return new RoomOpenDelegate.ValidationResult(false, "chat_messages(com.money.app.data.ChatMessage).\n"
                  + " Expected:\n" + _infoChatMessages + "\n"
                  + " Found:\n" + _existingChatMessages);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "transactions", "funds", "chat_messages");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "transactions", "funds", "chat_messages");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TransactionDao.class, TransactionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FundDao.class, FundDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatMessageDao.class, ChatMessageDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TransactionDao transactionDao() {
    if (_transactionDao != null) {
      return _transactionDao;
    } else {
      synchronized(this) {
        if(_transactionDao == null) {
          _transactionDao = new TransactionDao_Impl(this);
        }
        return _transactionDao;
      }
    }
  }

  @Override
  public FundDao fundDao() {
    if (_fundDao != null) {
      return _fundDao;
    } else {
      synchronized(this) {
        if(_fundDao == null) {
          _fundDao = new FundDao_Impl(this);
        }
        return _fundDao;
      }
    }
  }

  @Override
  public ChatMessageDao chatMessageDao() {
    if (_chatMessageDao != null) {
      return _chatMessageDao;
    } else {
      synchronized(this) {
        if(_chatMessageDao == null) {
          _chatMessageDao = new ChatMessageDao_Impl(this);
        }
        return _chatMessageDao;
      }
    }
  }
}
