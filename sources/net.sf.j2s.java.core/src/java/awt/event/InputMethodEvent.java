/*
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

package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Event;
import java.text.AttributedCharacterIterator;

/**
 * Input method events contain information about text that is being
 * composed using an input method. Whenever the text changes, the
 * input method sends an event. If the text component that's currently
 * using the input method is an active client, the event is dispatched
 * to that component. Otherwise, it is dispatched to a separate
 * composition window.
 *
 * <p>
 * The text included with the input method event consists of two parts:
 * committed text and composed text. Either part may be empty. The two
 * parts together replace any uncommitted composed text sent in previous events,
 * or the currently selected committed text.
 * Committed text should be integrated into the text component's persistent
 * data, it will not be sent again. Composed text may be sent repeatedly,
 * with changes to reflect the user's editing operations. Committed text
 * always precedes composed text.
 *
 * @author JavaSoft Asia/Pacific
 * @since 1.2
 */

public class InputMethodEvent extends AWTEvent {

	public InputMethodEvent(Event event) {
		super(event);
		// TODO Auto-generated constructor stub
	}
//
//    /**
//     * Serial Version ID.
//     */
//    //private static final long serialVersionUID = 4727190874778922661L;
//
    /**
     * Marks the first integer id for the range of input method event ids.
     */
    public static final int INPUT_METHOD_FIRST = 1100;

    /**
     * The event type indicating changed input method text. This event is
     * generated by input methods while processing input.
     */
    public static final int INPUT_METHOD_TEXT_CHANGED = INPUT_METHOD_FIRST;

    /**
     * The event type indicating a changed insertion point in input method text.
     * This event is
     * generated by input methods while processing input if only the caret changed.
     */
    public static final int CARET_POSITION_CHANGED = INPUT_METHOD_FIRST + 1;

    /**
     * Marks the last integer id for the range of input method event ids.
     */
    public static final int INPUT_METHOD_LAST = INPUT_METHOD_FIRST + 1;

    /**
     * The time stamp that indicates when the event was created.
     *
     * @serial
     * @see #getWhen
     * @since 1.4
     */
    long when;

//    // Text object
    private transient AttributedCharacterIterator text;
    private transient int committedCharacterCount;
//    private transient TextHitInfo caret;
//    private transient TextHitInfo visiblePosition;
//
//    /**
//     * Constructs an <code>InputMethodEvent</code> with the specified
//     * source component, type, time, text, caret, and visiblePosition.
//     * <p>
//     * The offsets of caret and visiblePosition are relative to the current
//     * composed text; that is, the composed text within <code>text</code>
//     * if this is an <code>INPUT_METHOD_TEXT_CHANGED</code> event,
//     * the composed text within the <code>text</code> of the
//     * preceding <code>INPUT_METHOD_TEXT_CHANGED</code> event otherwise.
//     * <p>Note that passing in an invalid <code>id</code> results in
//     * unspecified behavior. This method throws an
//     * <code>IllegalArgumentException</code> if <code>source</code>
//     * is <code>null</code>.
//     *
//     * @param source the object where the event originated
//     * @param id the event type
//     * @param when a long integer that specifies the time the event occurred
//     * @param text the combined committed and composed text,
//     *      committed text first; must be <code>null</code>
//     *      when the event type is <code>CARET_POSITION_CHANGED</code>;
//     *      may be <code>null</code> for
//     *      <code>INPUT_METHOD_TEXT_CHANGED</code> if there's no
//     *      committed or composed text
//     * @param committedCharacterCount the number of committed
//     *      characters in the text
//     * @param caret the caret (a.k.a. insertion point);
//     *      <code>null</code> if there's no caret within current
//     *      composed text
//     * @param visiblePosition the position that's most important
//     *      to be visible; <code>null</code> if there's no
//     *      recommendation for a visible position within current
//     *      composed text
//     * @throws IllegalArgumentException if <code>id</code> is not
//     *      in the range
//     *      <code>INPUT_METHOD_FIRST</code>..<code>INPUT_METHOD_LAST</code>;
//     *      or if id is <code>CARET_POSITION_CHANGED</code> and
//     *      <code>text</code> is not <code>null</code>;
//     *      or if <code>committedCharacterCount</code> is not in the range
//     *      <code>0</code>..<code>(text.getEndIndex() - text.getBeginIndex())</code>
//     * @throws IllegalArgumentException if <code>source</code> is null
//     *
//     * @since 1.4
//     */
//    public InputMethodEvent(Component source, int id, long when,
//            AttributedCharacterIterator text, int committedCharacterCount,
//            TextHitInfo caret, TextHitInfo visiblePosition) {
//        super(source, id);
//        if (id < INPUT_METHOD_FIRST || id > INPUT_METHOD_LAST) {
//            throw new IllegalArgumentException("id outside of valid range");
//        }
//
//        if (id == CARET_POSITION_CHANGED && text != null) {
//            throw new IllegalArgumentException("text must be null for CARET_POSITION_CHANGED");
//        }
//
//        this.when = when;
//        this.text = text;
//        int textLength = 0;
//        if (text != null) {
//            textLength = text.getEndIndex() - text.getBeginIndex();
//        }
//
//        if (committedCharacterCount < 0 || committedCharacterCount > textLength) {
//            throw new IllegalArgumentException("committedCharacterCount outside of valid range");
//        }
//        this.committedCharacterCount = committedCharacterCount;
//
//        this.caret = caret;
//        this.visiblePosition = visiblePosition;
//   }
//
//    /**
//     * Constructs an <code>InputMethodEvent</code> with the specified
//     * source component, type, text, caret, and visiblePosition.
//     * <p>
//     * The offsets of caret and visiblePosition are relative to the current
//     * composed text; that is, the composed text within <code>text</code>
//     * if this is an <code>INPUT_METHOD_TEXT_CHANGED</code> event,
//     * the composed text within the <code>text</code> of the
//     * preceding <code>INPUT_METHOD_TEXT_CHANGED</code> event otherwise.
//     * The time stamp for this event is initialized by invoking
//     * {@link java.awt.EventQueue#getMostRecentEventTime()}.
//     * <p>Note that passing in an invalid <code>id</code> results in
//     * unspecified behavior. This method throws an
//     * <code>IllegalArgumentException</code> if <code>source</code>
//     * is <code>null</code>.
//     *
//     * @param source the object where the event originated
//     * @param id the event type
//     * @param text the combined committed and composed text,
//     *      committed text first; must be <code>null</code>
//     *      when the event type is <code>CARET_POSITION_CHANGED</code>;
//     *      may be <code>null</code> for
//     *      <code>INPUT_METHOD_TEXT_CHANGED</code> if there's no
//     *      committed or composed text
//     * @param committedCharacterCount the number of committed
//     *      characters in the text
//     * @param caret the caret (a.k.a. insertion point);
//     *      <code>null</code> if there's no caret within current
//     *      composed text
//     * @param visiblePosition the position that's most important
//     *      to be visible; <code>null</code> if there's no
//     *      recommendation for a visible position within current
//     *      composed text
//     * @throws IllegalArgumentException if <code>id</code> is not
//     *      in the range
//     *      <code>INPUT_METHOD_FIRST</code>..<code>INPUT_METHOD_LAST</code>;
//     *      or if id is <code>CARET_POSITION_CHANGED</code> and
//     *      <code>text</code> is not <code>null</code>;
//     *      or if <code>committedCharacterCount</code> is not in the range
//     *      <code>0</code>..<code>(text.getEndIndex() - text.getBeginIndex())</code>
//     * @throws IllegalArgumentException if <code>source</code> is null
//     */
//    public InputMethodEvent(Component source, int id,
//            AttributedCharacterIterator text, int committedCharacterCount,
//            TextHitInfo caret, TextHitInfo visiblePosition) {
//        this(source, id,
//                getMostRecentEventTimeForSource(source),
//                text, committedCharacterCount,
//                caret, visiblePosition);
//    }
//
//    /**
//     * Constructs an <code>InputMethodEvent</code> with the
//     * specified source component, type, caret, and visiblePosition.
//     * The text is set to <code>null</code>,
//     * <code>committedCharacterCount</code> to 0.
//     * <p>
//     * The offsets of <code>caret</code> and <code>visiblePosition</code>
//     * are relative to the current composed text; that is,
//     * the composed text within the <code>text</code> of the
//     * preceding <code>INPUT_METHOD_TEXT_CHANGED</code> event if the
//     * event being constructed as a <code>CARET_POSITION_CHANGED</code> event.
//     * For an <code>INPUT_METHOD_TEXT_CHANGED</code> event without text,
//     * <code>caret</code> and <code>visiblePosition</code> must be
//     * <code>null</code>.
//     * The time stamp for this event is initialized by invoking
//     * {@link java.awt.EventQueue#getMostRecentEventTime()}.
//     * <p>Note that passing in an invalid <code>id</code> results in
//     * unspecified behavior. This method throws an
//     * <code>IllegalArgumentException</code> if <code>source</code>
//     * is <code>null</code>.
//     *
//     * @param source the object where the event originated
//     * @param id the event type
//     * @param caret the caret (a.k.a. insertion point);
//     *      <code>null</code> if there's no caret within current
//     *      composed text
//     * @param visiblePosition the position that's most important
//     *      to be visible; <code>null</code> if there's no
//     *      recommendation for a visible position within current
//     *      composed text
//     * @throws IllegalArgumentException if <code>id</code> is not
//     *      in the range
//     *      <code>INPUT_METHOD_FIRST</code>..<code>INPUT_METHOD_LAST</code>
//     * @throws IllegalArgumentException if <code>source</code> is null
//     */
//    public InputMethodEvent(Component source, int id, TextHitInfo caret,
//            TextHitInfo visiblePosition) {
//        this(source, id,
//                getMostRecentEventTimeForSource(source),
//                null, 0, caret, visiblePosition);
//    }
//
    /**
     * Gets the combined committed and composed text.
     * Characters from index 0 to index <code>getCommittedCharacterCount() - 1</code> are committed
     * text, the remaining characters are composed text.
     *
     * @return the text.
     * Always null for CARET_POSITION_CHANGED;
     * may be null for INPUT_METHOD_TEXT_CHANGED if there's no composed or committed text.
     */
    public AttributedCharacterIterator getText() {
        return text;
    }

    /**
     * Gets the number of committed characters in the text.
     */
    public int getCommittedCharacterCount() {
        return committedCharacterCount;
    }

//    /**
//     * Gets the caret.
//     * <p>
//     * The offset of the caret is relative to the current
//     * composed text; that is, the composed text within getText()
//     * if this is an <code>INPUT_METHOD_TEXT_CHANGED</code> event,
//     * the composed text within getText() of the
//     * preceding <code>INPUT_METHOD_TEXT_CHANGED</code> event otherwise.
//     *
//     * @return the caret (a.k.a. insertion point).
//     * Null if there's no caret within current composed text.
//     */
//    public TextHitInfo getCaret() {
//        return caret;
//    }
//
//    /**
//     * Gets the position that's most important to be visible.
//     * <p>
//     * The offset of the visible position is relative to the current
//     * composed text; that is, the composed text within getText()
//     * if this is an <code>INPUT_METHOD_TEXT_CHANGED</code> event,
//     * the composed text within getText() of the
//     * preceding <code>INPUT_METHOD_TEXT_CHANGED</code> event otherwise.
//     *
//     * @return the position that's most important to be visible.
//     * Null if there's no recommendation for a visible position within current composed text.
//     */
//    public TextHitInfo getVisiblePosition() {
//        return visiblePosition;
//    }
//
//    /**
//     * Consumes this event so that it will not be processed
//     * in the default manner by the source which originated it.
//     */
//    public void consume() {
//        consumed = true;
//    }
//
//    /**
//     * Returns whether or not this event has been consumed.
//     * @see #consume
//     */
//    public boolean isConsumed() {
//        return consumed;
//    }
//
    /**
     * Returns the time stamp of when this event occurred.
     *
     * @return this event's timestamp
     * @since 1.4
     */
    public long getWhen() {
      return when;
    }
//
//    /**
//     * Returns a parameter string identifying this event.
//     * This method is useful for event-logging and for debugging.
//     * It contains the event ID in text form, the characters of the
//     * committed and composed text
//     * separated by "+", the number of committed characters,
//     * the caret, and the visible position.
//     *
//     * @return a string identifying the event and its attributes
//     */
//    public String paramString() {
//        String typeStr;
//        switch(id) {
//          case INPUT_METHOD_TEXT_CHANGED:
//              typeStr = "INPUT_METHOD_TEXT_CHANGED";
//              break;
//          case CARET_POSITION_CHANGED:
//              typeStr = "CARET_POSITION_CHANGED";
//              break;
//          default:
//              typeStr = "unknown type";
//        }
//
//        String textString;
//        if (text == null) {
//            textString = "no text";
//        } else {
//            StringBuilder textBuffer = new StringBuilder("\"");
//            int committedCharacterCount = this.committedCharacterCount;
//            char c = text.first();
//            while (committedCharacterCount-- > 0) {
//                textBuffer.append(c);
//                c = text.next();
//            }
//            textBuffer.append("\" + \"");
//            while (c != CharacterIterator.DONE) {
//                textBuffer.append(c);
//                c = text.next();
//            }
//            textBuffer.append("\"");
//            textString = textBuffer.toString();
//        }
//
//        String countString = committedCharacterCount + " characters committed";
//
//        String caretString;
//        if (caret == null) {
//            caretString = "no caret";
//        } else {
//            caretString = "caret: " + caret.toString();
//        }
//
//        String visiblePositionString;
//        if (visiblePosition == null) {
//            visiblePositionString = "no visible position";
//        } else {
//            visiblePositionString = "visible position: " + visiblePosition.toString();
//        }
//
//        return typeStr + ", " + textString + ", " + countString + ", " + caretString + ", " + visiblePositionString;
//    }
//
//    /**
//     * Initializes the <code>when</code> field if it is not present in the
//     * object input stream. In that case, the field will be initialized by
//     * invoking {@link java.awt.EventQueue#getMostRecentEventTime()}.
//     */
//    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
//        s.defaultReadObject();
//        if (when == 0) {
//            // Can't use getMostRecentEventTimeForSource because source is always null during deserialization
//            when = EventQueue.getMostRecentEventTime();
//        }
//    }
//
//    /**
//     * Get the most recent event time in the {@code EventQueue} which the {@code source}
//     * belongs to.
//     *
//     * @param source the source of the event
//     * @exception  IllegalArgumentException  if source is null.
//     * @return most recent event time in the {@code EventQueue}
//     */
//    private static long getMostRecentEventTimeForSource(Object source) {
//        if (source == null) {
//            // throw the IllegalArgumentException to conform to EventObject spec
//            throw new IllegalArgumentException("null source");
//        }
//        AppContext appContext = SunToolkit.targetToAppContext(source);
//        EventQueue eventQueue = SunToolkit.getSystemEventQueueImplPP(appContext);
//        return AWTAccessor.getEventQueueAccessor().getMostRecentEventTime(eventQueue);
//    }
}
