package com.money.app.data;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class ChatMessageDao_Impl implements ChatMessageDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<ChatMessage> __insertAdapterOfChatMessage;

  public ChatMessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfChatMessage = new EntityInsertAdapter<ChatMessage>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chat_messages` (`id`,`text`,`isUser`,`timestamp`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ChatMessage entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getText() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getText());
        }
        final int _tmp = entity.isUser() ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindLong(4, entity.getTimestamp());
      }
    };
  }

  @Override
  public Object insert(final ChatMessage message, final Continuation<? super Unit> arg1) {
    if (message == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfChatMessage.insert(_connection, message);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object getAllMessages(final Continuation<? super List<ChatMessage>> arg0) {
    final String _sql = "SELECT * FROM chat_messages ORDER BY timestamp ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfText = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "text");
        final int _columnIndexOfIsUser = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isUser");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final List<ChatMessage> _result = new ArrayList<ChatMessage>();
        while (_stmt.step()) {
          final ChatMessage _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpText;
          if (_stmt.isNull(_columnIndexOfText)) {
            _tmpText = null;
          } else {
            _tmpText = _stmt.getText(_columnIndexOfText);
          }
          final boolean _tmpIsUser;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsUser));
          _tmpIsUser = _tmp != 0;
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          _item = new ChatMessage(_tmpId,_tmpText,_tmpIsUser,_tmpTimestamp);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, arg0);
  }

  @Override
  public Object clearHistory(final Continuation<? super Unit> arg0) {
    final String _sql = "DELETE FROM chat_messages";
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
