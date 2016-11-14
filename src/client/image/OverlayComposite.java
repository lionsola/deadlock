/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package client.image;

import java.awt.*;
import java.awt.image.*;

public final class OverlayComposite extends RGBComposite {

	public OverlayComposite( float alpha ) {
        super( alpha );
	}

	public CompositeContext createContext( ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints ) {
		return new Context( extraAlpha, srcColorModel, dstColorModel );
	}

    static class Context extends RGBCompositeContext {
        public Context( float alpha, ColorModel srcColorModel, ColorModel dstColorModel ) {
            super( alpha, srcColorModel, dstColorModel );
        }

		@Override
		public void composeRGB(int[] src, int srcChannels, int[] dst, int dstChannels, float alpha) {
			int w = src.length;

			for ( int si = 0, di = 0; si < src.length && di<dst.length; si += srcChannels, di += dstChannels ) {
                int sr = src[si];
                int dir = dst[di];
                int sg = src[si+1];
                int dig = dst[di+1];
                int sb = src[si+2];
                int dib = dst[di+2];
                
                //int sa = src[i+3];
                //int dia = dst[i+3];
                int dor, dog, dob;

                int t;
                if ( dir < 128 ) {
                    t = dir * sr + 0x80;
                    dor = 2 * (((t >> 8) + t) >> 8);
                } else {
                    t = (255-dir) * (255-sr) + 0x80;
                    dor = 2 * (255 - ( ((t >> 8) + t) >> 8 ));
                }
                if ( dig < 128 ) {
                    t = dig * sg + 0x80;
                    dog = 2 * (((t >> 8) + t) >> 8);
                } else {
                    t = (255-dig) * (255-sg) + 0x80;
                    dog = 2 * (255 - ( ((t >> 8) + t) >> 8 ));
                }
                if ( dib < 128 ) {
                    t = dib * sb + 0x80;
                    dob = 2 * (((t >> 8) + t) >> 8);
                } else {
                    t = (255-dib) * (255-sb) + 0x80;
                    dob = 2 * (255 - ( ((t >> 8) + t) >> 8 ));
                }

                float a = alpha;
                float ac = 1-a;

                dst[di] = (int)(a*dor + ac*dir);
                dst[di+1] = (int)(a*dog + ac*dig);
                dst[di+2] = (int)(a*dob + ac*dib);
            }
		}
    }

}