package com.money.app.data;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Transaction> __insertAdapterOfTransaction;

  private final EntityDeleteOrUpdateAdapter<Transaction> __deleteAdapterOfTransaction;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfTransaction = new EntityInsertAdapter<Transaction>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `transactions` (`id`,`syncId`,`userId`,`amount`,`category`,`date`,`description`,`imagePath`,`isExpense`,`rating`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final Transaction entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getSyncId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getSyncId());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getUserId());
        }
        statement.bindDouble(4, entity.getAmount());
        if (entity.getCategory() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getCategory());
        }
        if (entity.getDate() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getDate());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getDescription());
        }
        if (entity.getImagePath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getImagePath());
        }
        final int _tmp = entity.isExpense() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getRating());
        statement.bindLong(11, entity.getTimestamp());
      }
    };
    this.__deleteAdapterOfTransaction = new EntityDeleteOrUpdateAdapter<Transaction>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final Transaction entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Transaction transaction,
      final Continuation<? super Long> $completion) {
    if (transaction == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfTransaction.insertAndReturnId(_connection, transaction);
    }, $completion);
  }

  @Override
  public Object delete(final Transaction transaction,
      final Continuation<? super Unit> $completion) {
    if (transaction == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfTransaction.handle(_connection, transaction);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getLatestTransaction(final Continuation<? super Transaction> $completion) {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfSyncId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "syncId");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfImagePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "imagePath");
        final int _columnIndexOfIsExpense = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isExpense");
        final int _columnIndexOfRating = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "rating");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final Transaction _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpSyncId;
          if (_stmt.isNull(_columnIndexOfSyncId)) {
            _tmpSyncId = null;
          } else {
            _tmpSyncId = _stmt.getText(_columnIndexOfSyncId);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpCategory;
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null;
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory);
          }
          final String _tmpDate;
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null;
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpImagePath;
          if (_stmt.isNull(_columnIndexOfImagePath)) {
            _tmpImagePath = null;
          } else {
            _tmpImagePath = _stmt.getText(_columnIndexOfImagePath);
          }
          final boolean _tmpIsExpense;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsExpense));
          _tmpIsExpense = _tmp != 0;
          final int _tmpRating;
          _tmpRating = (int) (_stmt.getLong(_columnIndexOfRating));
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          _result = new Transaction(_tmpId,_tmpSyncId,_tmpUserId,_tmpAmount,_tmpCategory,_tmpDate,_tmpDescription,_tmpImagePath,_tmpIsExpense,_tmpRating,_tmpTimestamp);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getAllTransactions(final Continuation<? super List<Transaction>> $completion) {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfSyncId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "syncId");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfImagePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "imagePath");
        final int _columnIndexOfIsExpense = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isExpense");
        final int _columnIndexOfRating = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "rating");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final List<Transaction> _result = new ArrayList<Transaction>();
        while (_stmt.step()) {
          final Transaction _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpSyncId;
          if (_stmt.isNull(_columnIndexOfSyncId)) {
            _tmpSyncId = null;
          } else {
            _tmpSyncId = _stmt.getText(_columnIndexOfSyncId);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpCategory;
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null;
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory);
          }
          final String _tmpDate;
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null;
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpImagePath;
          if (_stmt.isNull(_columnIndexOfImagePath)) {
            _tmpImagePath = null;
          } else {
            _tmpImagePath = _stmt.getText(_columnIndexOfImagePath);
          }
          final boolean _tmpIsExpense;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsExpense));
          _tmpIsExpense = _tmp != 0;
          final int _tmpRating;
          _tmpRating = (int) (_stmt.getLong(_columnIndexOfRating));
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          _item = new Transaction(_tmpId,_tmpSyncId,_tmpUserId,_tmpAmount,_tmpCategory,_tmpDate,_tmpDescription,_tmpImagePath,_tmpIsExpense,_tmpRating,_tmpTimestamp);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<Transaction>> getAllTransactionsFlow() {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"transactions"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfSyncId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "syncId");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfImagePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "imagePath");
        final int _columnIndexOfIsExpense = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isExpense");
        final int _columnIndexOfRating = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "rating");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final List<Transaction> _result = new ArrayList<Transaction>();
        while (_stmt.step()) {
          final Transaction _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpSyncId;
          if (_stmt.isNull(_columnIndexOfSyncId)) {
            _tmpSyncId = null;
          } else {
            _tmpSyncId = _stmt.getText(_columnIndexOfSyncId);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpCategory;
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null;
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory);
          }
          final String _tmpDate;
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null;
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpImagePath;
          if (_stmt.isNull(_columnIndexOfImagePath)) {
            _tmpImagePath = null;
          } else {
            _tmpImagePath = _stmt.getText(_columnIndexOfImagePath);
          }
          final boolean _tmpIsExpense;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsExpense));
          _tmpIsExpense = _tmp != 0;
          final int _tmpRating;
          _tmpRating = (int) (_stmt.getLong(_columnIndexOfRating));
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          _item = new Transaction(_tmpId,_tmpSyncId,_tmpUserId,_tmpAmount,_tmpCategory,_tmpDate,_tmpDescription,_tmpImagePath,_tmpIsExpense,_tmpRating,_tmpTimestamp);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getExpensesInTimeRange(final long startTime, final long endTime,
      final Continuation<? super List<Transaction>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE isExpense = 1 AND timestamp >= ? AND timestamp <= ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startTime);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, endTime);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfSyncId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "syncId");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfImagePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "imagePath");
        final int _columnIndexOfIsExpense = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isExpense");
        final int _columnIndexOfRating = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "rating");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final List<Transaction> _result = new ArrayList<Transaction>();
        while (_stmt.step()) {
          final Transaction _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpSyncId;
          if (_stmt.isNull(_columnIndexOfSyncId)) {
            _tmpSyncId = null;
          } else {
            _tmpSyncId = _stmt.getText(_columnIndexOfSyncId);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpCategory;
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null;
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory);
          }
          final String _tmpDate;
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null;
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpImagePath;
          if (_stmt.isNull(_columnIndexOfImagePath)) {
            _tmpImagePath = null;
          } else {
            _tmpImagePath = _stmt.getText(_columnIndexOfImagePath);
          }
          final boolean _tmpIsExpense;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsExpense));
          _tmpIsExpense = _tmp != 0;
          final int _tmpRating;
          _tmpRating = (int) (_stmt.getLong(_columnIndexOfRating));
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          _item = new Transaction(_tmpId,_tmpSyncId,_tmpUserId,_tmpAmount,_tmpCategory,_tmpDate,_tmpDescription,_tmpImagePath,_tmpIsExpense,_tmpRating,_tmpTimestamp);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object getTransactionsInTimeRange(final long startTime, final long endTime,
      final Continuation<? super List<Transaction>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE timestamp >= ? AND timestamp <= ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, startTime);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, endTime);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfSyncId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "syncId");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfImagePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "imagePath");
        final int _columnIndexOfIsExpense = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isExpense");
        final int _columnIndexOfRating = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "rating");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final List<Transaction> _result = new ArrayList<Transaction>();
        while (_stmt.step()) {
          final Transaction _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpSyncId;
          if (_stmt.isNull(_columnIndexOfSyncId)) {
            _tmpSyncId = null;
          } else {
            _tmpSyncId = _stmt.getText(_columnIndexOfSyncId);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpCategory;
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null;
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory);
          }
          final String _tmpDate;
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null;
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpImagePath;
          if (_stmt.isNull(_columnIndexOfImagePath)) {
            _tmpImagePath = null;
          } else {
            _tmpImagePath = _stmt.getText(_columnIndexOfImagePath);
          }
          final boolean _tmpIsExpense;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsExpense));
          _tmpIsExpense = _tmp != 0;
          final int _tmpRating;
          _tmpRating = (int) (_stmt.getLong(_columnIndexOfRating));
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          _item = new Transaction(_tmpId,_tmpSyncId,_tmpUserId,_tmpAmount,_tmpCategory,_tmpDate,_tmpDescription,_tmpImagePath,_tmpIsExpense,_tmpRating,_tmpTimestamp);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object searchTransactions(final String query,
      final Continuation<? super List<Transaction>> $completion) {
    final String _sql = "SELECT * FROM transactions WHERE category LIKE ? OR description LIKE ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        _argIndex = 2;
        if (query == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, query);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfSyncId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "syncId");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "amount");
        final int _columnIndexOfCategory = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "category");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfImagePath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "imagePath");
        final int _columnIndexOfIsExpense = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isExpense");
        final int _columnIndexOfRating = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "rating");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final List<Transaction> _result = new ArrayList<Transaction>();
        while (_stmt.step()) {
          final Transaction _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpSyncId;
          if (_stmt.isNull(_columnIndexOfSyncId)) {
            _tmpSyncId = null;
          } else {
            _tmpSyncId = _stmt.getText(_columnIndexOfSyncId);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final double _tmpAmount;
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount);
          final String _tmpCategory;
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null;
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory);
          }
          final String _tmpDate;
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null;
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate);
          }
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpImagePath;
          if (_stmt.isNull(_columnIndexOfImagePath)) {
            _tmpImagePath = null;
          } else {
            _tmpImagePath = _stmt.getText(_columnIndexOfImagePath);
          }
          final boolean _tmpIsExpense;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsExpense));
          _tmpIsExpense = _tmp != 0;
          final int _tmpRating;
          _tmpRating = (int) (_stmt.getLong(_columnIndexOfRating));
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          _item = new Transaction(_tmpId,_tmpSyncId,_tmpUserId,_tmpAmount,_tmpCategory,_tmpDate,_tmpDescription,_tmpImagePath,_tmpIsExpense,_tmpRating,_tmpTimestamp);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM transactions";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
