/*
 * Some portions of this file have been modified by Robert Hanson hansonr.at.stolaf.edu 2012-2017
 * for use in SwingJS via transpilation into JavaScript using Java2Script.
 *
 * Copyright (c) 2000, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.awt;

import java.util.LinkedList;

import swingjs.JSToolkit;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

/**
 * A mechanism for ensuring that a series of AWTEvents are executed in a
 * precise order, even across multiple AppContexts. The nested events will be
 * dispatched in the order in which their wrapping SequencedEvents were
 * constructed. The only exception to this rule is if the peer of the target of
 * the nested event was destroyed (with a call to Component.removeNotify)
 * before the wrapping SequencedEvent was able to be dispatched. In this case,
 * the nested event is never dispatched.
 *
 * @author David Mendenhall
 */
class SequencedEvent extends AWTEvent implements ActiveEvent {
    /*
     * serialVersionUID
     */
    private static final long serialVersionUID = 547742659238625067L;

    private static final int ID =
        java.awt.event.FocusEvent.FOCUS_LAST + 1;
    private static final LinkedList list = new LinkedList();

    private final AWTEvent nested;
    private AppContext appContext;
    private boolean disposed;

    /**
     * Constructs a new SequencedEvent which will dispatch the specified
     * nested event.
     *
     * @param nested the AWTEvent which this SequencedEvent's dispatch()
     *        method will dispatch
     */
    public SequencedEvent(AWTEvent nested) {
        super(nested.getSource(), ID);
        this.nested = nested;
        synchronized (SequencedEvent.class) {
            list.add(this);
        }
    }

    /**
     * Dispatches the nested event after all previous nested events have been
     * dispatched or disposed. If this method is invoked before all previous nested events
     * have been dispatched, then this method blocks until such a point is
     * reached.
     * While waiting disposes nested events to disposed AppContext
     *
     * NOTE: Locking protocol.  Since dispose() can get EventQueue lock,
     * dispatch() shall never call dispose() while holding the lock on the list,
     * as EventQueue lock is held during dispatching.  The locks should be acquired
     * in the same order.
     */
    @Override
		public final void dispatch() {
        try {
            appContext = AppContext.getAppContext();

            if (getFirst() != this) {
                if (EventQueue.isDispatchThread()) {
                    EventDispatchThread edt = (EventDispatchThread)
                        Thread.currentThread();
                    edt.pumpEvents(SentEvent.ID, new Conditional() {
                        @Override
												public boolean evaluate() {
                            return !SequencedEvent.this.isFirstOrDisposed();
                        }
                    });
                } else {
                    while(!isFirstOrDisposed()) {
                        synchronized (SequencedEvent.class) {
                            try {
                          	  JSToolkit.warn("Cannot wait in SequenceEvent");
                                SequencedEvent.class.wait(1000);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                }
            }

            if (!disposed) {
// SwingJS                 KeyboardFocusManager.getCurrentKeyboardFocusManager().
//                    setCurrentSequencedEvent(this);
                Toolkit.getEventQueue().dispatchEvent(nested);
            }
        } finally {
            dispose();
        }
    }

    /**
     * true only if event exists and nested source appContext is disposed.
     */
    private final static boolean isOwnerAppContextDisposed(SequencedEvent se) {
        if (se != null) {
            Object target = se.nested.getSource();
            if (target instanceof Component) {
                return ((Component)target).appContext.isDisposed();
            }
        }
        return false;
    }

    /**
     * Sequenced events are dispatched in order, so we cannot dispatch
     * until we are the first sequenced event in the queue (i.e. it's our
     * turn).  But while we wait for our turn to dispatch, the event
     * could have been disposed for a number of reasons.
     */
    public final boolean isFirstOrDisposed() {
        if (disposed) {
            return true;
        }
        // getFirstWithContext can dispose this
        return this == getFirstWithContext() || disposed;
    }

    private final synchronized static SequencedEvent getFirst() {
        return (SequencedEvent)list.getFirst();
    }

    /* Disposes all events from disposed AppContext
     * return first valid event
     */
    private final static SequencedEvent getFirstWithContext() {
        SequencedEvent first = getFirst();
        while(isOwnerAppContextDisposed(first)) {
            first.dispose();
            first = getFirst();
        }
        return first;
    }

    /**
     * Disposes of this instance. This method is invoked once the nested event
     * has been dispatched and handled, or when the peer of the target of the
     * nested event has been disposed with a call to Component.removeNotify.
     *
     * NOTE: Locking protocol.  Since SunToolkit.postEvent can get EventQueue lock,
     * it shall never be called while holding the lock on the list,
     * as EventQueue lock is held during dispatching and dispatch() will get
     * lock on the list. The locks should be acquired in the same order.
     */
    final void dispose() {
      synchronized (SequencedEvent.class) {
            if (disposed) {
                return;
            }
// SwingJS            if (KeyboardFocusManager.getCurrentKeyboardFocusManager().
//                    getCurrentSequencedEvent() == this) {
//                KeyboardFocusManager.getCurrentKeyboardFocusManager().
//                    setCurrentSequencedEvent(null);
//            }
            disposed = true;
        }
        // Wake myself up
        if (appContext != null) {
            SunToolkit.postEvent(appContext, new SentEvent());
        }

        SequencedEvent next = null;

        synchronized (SequencedEvent.class) {
          SequencedEvent.class.notifyAll();

          if (list.getFirst() == this) {
              list.removeFirst();

              if (!list.isEmpty()) {
                    next = (SequencedEvent)list.getFirst();
              }
          } else {
              list.remove(this);
          }
      }
        // Wake up waiting threads
        if (next != null && next.appContext != null) {
            SunToolkit.postEvent(next.appContext, new SentEvent());
        }
    }
}