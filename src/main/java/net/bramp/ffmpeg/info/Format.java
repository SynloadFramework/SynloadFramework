package net.bramp.ffmpeg.info;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Information about supported Format
 * @author bramp
 *
 */
public class Format {
	final String name;
	final String longName;

    final boolean canDemux;
    final boolean canMux;

	public Format(String name, String longName, String flags) {
		this.name = Preconditions.checkNotNull(name).trim();
		this.longName  = Preconditions.checkNotNull(longName).trim();

        /*
         D. = Demuxing supported
         .E = Muxing supported
         */
        Preconditions.checkNotNull(flags);
        Preconditions.checkArgument(flags.length() == 2, "Format flags is invalid '{}'", flags);
        canDemux = flags.charAt(0) == 'D';
        canMux   = flags.charAt(1) == 'E';
	}

	@Override
	public String toString() {
		return name + " " + longName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Format)) {
			return false;
		}

		Format that = (Format) obj;
		return Objects.equal(this.name, that.name) &&
                Objects.equal(this.longName, that.longName) &&
                (this.canMux == that.canMux) &&
                (this.canDemux == that.canDemux);
	}

    public String getName() {
        return name;
    }

    public String getLongName() {
        return longName;
    }

    public boolean getCanDemux() {
        return canDemux;
    }

    public boolean getCanMux() {
        return canMux;
    }
}
