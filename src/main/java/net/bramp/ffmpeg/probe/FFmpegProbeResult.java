package net.bramp.ffmpeg.probe;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.List;

/**
 * TODO Make this immutable
 */
@SuppressWarnings("serial")
public class FFmpegProbeResult implements Serializable{
	public FFmpegError error;
	public FFmpegFormat format;
	public List<FFmpegStream> streams;

    public FFmpegError getError() {
        return error;
    }

    public FFmpegFormat getFormat() {
        return format;
    }

    public List<FFmpegStream> getStreams() {
        return ImmutableList.copyOf(streams);
    }
}
