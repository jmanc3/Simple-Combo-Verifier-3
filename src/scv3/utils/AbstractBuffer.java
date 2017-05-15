package scv3.utils;

import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class AbstractBuffer {
    private RandomAccessFile aFile;
    private FileChannel inChannel;
    private MappedByteBuffer buffer;

    public AbstractBuffer(File file) throws IOException {
        aFile = new RandomAccessFile(file, "r");
        inChannel = aFile.getChannel();
        buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        buffer.load();
    }

    public void close() throws IOException {
        buffer.clear();
        inChannel.close();
        aFile.getChannel().close();
        aFile.close();
        unmap(buffer);
        buffer = null;
        inChannel = null;
        aFile = null;
    }

    public static void unmap(MappedByteBuffer buffer) {
        sun.misc.Cleaner cleaner = ((DirectBuffer) buffer).cleaner();
        cleaner.clean();
    }

    public char getNextChar() {
        return (char) buffer.get();
    }

    public int getLimit() {
        return buffer.limit();
    }

    public int getDivider() {

        return buffer.limit() / 8;
    }

}
