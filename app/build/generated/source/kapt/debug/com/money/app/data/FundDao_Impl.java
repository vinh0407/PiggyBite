package com.money.app.data;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class FundDao_Impl implements FundDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<Fund> __insertAdapterOfFund;

  private final Converters __converters = new Converters();

  private final EntityDeleteOrUpdateAdapter<Fund> __deleteAdapterOfFund;

  private final EntityDeleteOrUpdateAdapter<Fund> __updateAdapterOfFund;

  public FundDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFund = new EntityInsertAdapter<Fund>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `funds` (`id`,`syncId`,`ownerId`,`name`,`currentAmount`,`targetAmount`,`icon`,`createdDate`,`endDate`,`isPinned`,`isShared`,`members`,`memberContributions`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Fund entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getSyncId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getSyncId());
        }
        if (entity.getOwnerId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getOwnerId());
        }
        if (entity.getName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getName());
        }
        statement.bindDouble(5, entity.getCurrentAmount());
        statement.bindDouble(6, entity.getTargetAmount());
        if (entity.getIcon() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getIcon());
        }
        statement.bindLong(8, entity.getCreatedDate());
        statement.bindLong(9, entity.getEndDate());
        final int _tmp = entity.isPinned() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.isShared() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        final String _tmp_2 = __converters.fromList(entity.getMembers());
        if (_tmp_2 == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, _tmp_2);
        }
        final String _tmp_3 = __converters.fromMap(entity.getMemberContributions());
        if (_tmp_3 == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, _tmp_3);
        }
      }
    };
    this.__deleteAdapterOfFund = new EntityDeleteOrUpdateAdapter<Fund>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `funds` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Fund entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFund = new EntityDeleteOrUpdateAdapter<Fund>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `funds` SET `id` = ?,`syncId` = ?,`ownerId` = ?,`name` = ?,`currentAmount` = ?,`targetAmount` = ?,`icon` = ?,`createdDate` = ?,`endDate` = ?,`isPinned` = ?,`isShared` = ?,`members` = ?,`memberContributions` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, @NonNull final Fund entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getSyncId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getSyncId());
        }
        if (entity.getOwnerId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getOwnerId());
        }
        if (entity.getName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getName());
        }
        statement.bindDouble(5, entity.getCurrentAmount());
        statement.bindDouble(6, entity.getTargetAmount());
        if (entity.getIcon() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getIcon());
        }
        statement.bindLong(8, entity.getCreatedDate());
        statement.bindLong(9, entity.getEndDate());
        final int _tmp = entity.isPinned() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.isShared() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        final String _tmp_2 = __converters.fromList(entity.getMembers());
        if (_tmp_2 == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, _tmp_2);
        }
        final String _tmp_3 = __converters.fromMap(entity.getMemberContributions());
        if (_tmp_3 == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, _tmp_3);
        }
        statement.bindLong(14, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Fund fund, final Continuation<? super Unit> arg1) {
    if (fund == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfFund.insert(_connection, fund);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object delete(final Fund fund, final Continuation<? super Unit> arg1) {
    if (fund == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfFund.handle(_connection, fund);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object update(final Fund fund, final Continuation<? super Unit> arg1) {
    if (fund == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfFund.handle(_connection, fund);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object getAllFunds(final Continuation<? super List<Fund>> arg0) {
    final String _sql = "SELECT * FROM funds ORDER BY isPinned DESC, createdDate DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfSyncId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "syncId");
        final int _columnIndexOfOwnerId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "ownerId");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfCurrentAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "currentAmount");
        final int _columnIndexOfTargetAmount = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetAmount");
        final int _columnIndexOfIcon = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "icon");
        final int _columnIndexOfCreatedDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdDate");
        final int _columnIndexOfEndDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "endDate");
        final int _columnIndexOfIsPinned = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isPinned");
        final int _columnIndexOfIsShared = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isShared");
        final int _columnIndexOfMembers = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "members");
        final int _columnIndexOfMemberContributions = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "memberContributions");
        final List<Fund> _result = new ArrayList<Fund>();
        while (_stmt.step()) {
          final Fund _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpSyncId;
          if (_stmt.isNull(_columnIndexOfSyncId)) {
            _tmpSyncId = null;
          } else {
            _tmpSyncId = _stmt.getText(_columnIndexOfSyncId);
          }
          final String _tmpOwnerId;
          if (_stmt.isNull(_columnIndexOfOwnerId)) {
            _tmpOwnerId = null;
          } else {
            _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId);
          }
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final double _tmpCurrentAmount;
          _tmpCurrentAmount = _stmt.getDouble(_columnIndexOfCurrentAmount);
          final double _tmpTargetAmount;
          _tmpTargetAmount = _stmt.getDouble(_columnIndexOfTargetAmount);
          final String _tmpIcon;
          if (_stmt.isNull(_columnIndexOfIcon)) {
            _tmpIcon = null;
          } else {
            _tmpIcon = _stmt.getText(_columnIndexOfIcon);
          }
          final long _tmpCreatedDate;
          _tmpCreatedDate = _stmt.getLong(_columnIndexOfCreatedDate);
          final long _tmpEndDate;
          _tmpEndDate = _stmt.getLong(_columnIndexOfEndDate);
          final boolean _tmpIsPinned;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsPinned));
          _tmpIsPinned = _tmp != 0;
          final boolean _tmpIsShared;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfIsShared));
          _tmpIsShared = _tmp_1 != 0;
          final List<String> _tmpMembers;
          final String _tmp_2;
          if (_stmt.isNull(_columnIndexOfMembers)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _stmt.getText(_columnIndexOfMembers);
          }
          _tmpMembers = __converters.fromString(_tmp_2);
          final Map<String, Double> _tmpMemberContributions;
          final String _tmp_3;
          if (_stmt.isNull(_columnIndexOfMemberContributions)) {
            _tmp_3 = null;
          } else {
            _tmp_3 = _stmt.getText(_columnIndexOfMemberContributions);
          }
          _tmpMemberContributions = __converters.toMap(_tmp_3);
          _item = new Fund(_tmpId,_tmpSyncId,_tmpOwnerId,_tmpName,_tmpCurrentAmount,_tmpTargetAmount,_tmpIcon,_tmpCreatedDate,_tmpEndDate,_tmpIsPinned,_tmpIsShared,_tmpMembers,_tmpMemberContributions);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, arg0);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> arg0) {
    final String _sql = "DELETE FROM funds";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, arg0);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
