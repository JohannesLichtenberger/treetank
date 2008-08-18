/*
 * Copyright (c) 2008, Marc Kramis (Ph.D. Thesis), University of Konstanz
 * 
 * Patent Pending.
 * 
 * NO permission to use, copy, modify, and/or distribute this software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * $Id$
 */

package com.treetank.core;

import com.treetank.api.IDevice;
import com.treetank.api.IRevisionWriteCore;
import com.treetank.device.Device;
import com.treetank.shared.ByteArrayWriter;
import com.treetank.shared.FragmentReference;
import com.treetank.shared.RevisionReference;

public final class RevisionWriteCore implements IRevisionWriteCore {

  private final IDevice mDevice1;

  private final IDevice mDevice2;

  public RevisionWriteCore(final String device) {

    if (device == null || device.length() < 1) {
      throw new IllegalArgumentException(
          "Argument 'device' must not be null and longer than zero.");
    }

    mDevice1 = new Device(device + ".tt1", "rw");
    mDevice2 = new Device(device + ".tt2", "rw");
  }

  public final RevisionReference writeRevision(
      final long revision,
      final FragmentReference fragmentReference) {

    if ((revision < 1)) {
      throw new IllegalArgumentException(
          "Argument 'revision' must be greater than zero.");
    }

    if ((fragmentReference == null)
        || (fragmentReference.getOffset() < 1)
        || (fragmentReference.getLength() < 1)) {
      throw new IllegalArgumentException(
          "Argument 'fragmentReference' must not be null "
              + "and offset and length must be greater than zero.");
    }

    try {

      final RevisionReference revisionReference =
          new RevisionReference(
              fragmentReference.getOffset(),
              fragmentReference.getLength(),
              revision);

      final ByteArrayWriter writer = new ByteArrayWriter();
      revisionReference.serialise(writer);

      mDevice1.write(mDevice1.size(), writer);
      mDevice2.write(mDevice2.size(), writer);

      return revisionReference;

    } catch (Exception e) {
      throw new RuntimeException("RevisionWriteCore "
          + "could not write revision due to: "
          + e.toString());
    }
  }

}