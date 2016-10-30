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

public final class SoftHardLightComposite extends RGBComposite {

	public SoftHardLightComposite( float alpha ) {
        super( alpha );
	}

	public CompositeContext createContext( ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints ) {
		return new Context( extraAlpha, srcColorModel, dstColorModel );
	}

    static class Context extends RGBCompositeContext {
        public Context( float alpha, ColorModel srcColorModel, ColorModel dstColorModel ) {
            super( alpha, srcColorModel, dstColorModel );
        }

        public void composeRGB( int[] src, int srcChannels, int[] dst, int dstChannels, float alpha ) {
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
                
                int d;
                if (sr > 127) {
                	d = multiply255(sr, dir);
                    dor = d + multiply255(dir, 255 - multiply255(255-dir, 255-sr)-d);
                }
                else
                    dor = 2*multiply255(sr, dir);
                if (sg > 127) {
                	d = multiply255(sg, dig);
                    dog = d + multiply255(dig, 255 - multiply255(255-dig, 255-sg)-d);
                }
                else
                    dog = 2*multiply255(sg, dig);
                if (sb > 127) {
                	d = multiply255(sb, dib);
                    dob = d + multiply255(dib, 255 - multiply255(255-dib, 255-sb)-d);
                }
                else
                    dob = 2*multiply255(sb, dib);

                float a = alpha;//*sa/255f;
                float ac = 1-a;

                dst[di] = (int)(a*dor + ac*dir);
                dst[di+1] = (int)(a*dog + ac*dig);
                dst[di+2] = (int)(a*dob + ac*dib);
                //dst[i+3] = (int)(sa*alpha + dia*ac);
            }
        }
    }

}
