package net.bramp.ffmpeg.probe;

import java.io.Serializable;
import java.util.Map;

public class FFmpegFormat implements Serializable {
	public String filename;
	public int nb_streams;
	public String format_name;
	public String format_long_name;
	public double start_time;

	/**
	 * Duration in seconds
	 */
	public double duration;
	
	/**
	 * File size in bytes
	 */
	public long size;
	
	/**
	 * Bitrate
	 */
	public int bit_rate;

	public Map<String, String> tags;
}
