/**
 * Copyright (C) 2013 Oculus Info Inc. http://www.oculusinfo.com/
 *
 * <p>Released under the MIT License.
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oculus.aperture.layout;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import oculus.aperture.layout.yworks.YWorksFactory;

/**
 * Bindings for inclusion of graph layout provider using JGraphT.
 *
 * <p>This module was originally designed for yWorks yFiles but has been updated to use the
 * open-source JGraphT library (https://jgrapht.org/) which provides equivalent graph layout
 * algorithms without requiring a commercial license.
 *
 * <p>Supported layouts: - Circular layout - Radial layout - Organic (force-directed) layout -
 * Vertical tree layout - Horizontal tree layout
 */
public class YWorksLayoutModule extends AbstractModule {

  /* (non-Javadoc)
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    Multibinder<LayoutGraphFactory> binder =
        Multibinder.newSetBinder(binder(), LayoutGraphFactory.class);

    binder.addBinding().to(YWorksFactory.class);
  }
}
