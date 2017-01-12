package server.network;

import java.io.Serializable;
import java.nio.ByteBuffer;

import editor.dialogs.MissionDialog.DataType;

public class MissionVar implements Serializable {
	private static final long serialVersionUID = -1737519880440217970L;
	public final String name;
	public final DataType type;
	private byte[] value;
	
	public MissionVar(String name, DataType type) {
		this.name = name;
		this.type = type;
	}
	
	public void setValue(int[] value) {
		ByteBuffer buffer = ByteBuffer.allocate(value.length*2);
		for (int v:value) {
			buffer.putShort((short)v);
		}
		this.value = buffer.array();
	}
	
	public void setValue(float tx, float ty) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putFloat(tx);
		buffer.putFloat(ty);
		this.value = buffer.array();
	}
	
	public void setValue(int value) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(value);
		this.value = buffer.array();
	}
	
	public Object getValue() {
		if (value!=null) {
			ByteBuffer buffer = ByteBuffer.wrap(value);
			switch (type) {
				case Location:
					float[] loc = new float[2];
					loc[0] = buffer.getFloat();
					loc[1] = buffer.getFloat();
					return loc;
				case Time:
					int time = buffer.getInt();
					return time;
				case Character:
					int id = buffer.getInt();
					return id;
				default:
					return null;	
			}
		} else {
			return null;
		}
	}
}