
package org.treetank.xpath.functions;

import java.util.ArrayList;
import java.util.List;

import org.treetank.api.IAxis;
import org.treetank.api.IItem;
import org.treetank.api.IReadTransaction;
import org.treetank.utils.TypedValue;
import org.treetank.xpath.AtomicValue;
import org.treetank.xpath.functions.XPathError.ErrorType;
import org.treetank.xpath.types.Type;

public class Function {


  public static boolean ebv(final IAxis axis) {
    FuncDef ebv = FuncDef.BOOEAN;
    List<IAxis> param = new ArrayList<IAxis>();
    param.add(axis);
    IAxis bAxis = new FNBoolean(axis.getTransaction(), 
        param, ebv.getMin(), ebv.getMax(), 
        axis.getTransaction().keyForName(ebv.getReturnType()));
    if (bAxis.hasNext()) {
      bAxis.next();
      boolean result =Boolean.parseBoolean(bAxis.getTransaction().getValue());
      if (!bAxis.hasNext()) {
        bAxis.reset(axis.getTransaction().getNodeKey());
       
      
      return result;
      }
    }
      throw new IllegalStateException("This should not happen!"); //TODO!!  
  }
  
  public static boolean empty(final IReadTransaction rtx, final IAxis axis) {

    boolean result = !axis.hasNext();

    int itemKey = rtx.getItemList().addItem(new AtomicValue(result));
    rtx.moveTo(itemKey);
    return true;
  }

  public static boolean exactlyOne(final IReadTransaction rtx, final IAxis axis) {

    if (axis.hasNext()) {
      if (axis.hasNext()) {
        throw new XPathError(ErrorType.FORG0005);
      } else {
        int itemKey = rtx.getItemList().addItem(new AtomicValue(true));
        rtx.moveTo(itemKey); // only once

      }

    } else {
      throw new XPathError(ErrorType.FORG0005);
    }

    return true;
  }


  public static boolean exists(final IReadTransaction rtx, final IAxis axis) {

    boolean result = axis.hasNext();
    int itemKey = rtx.getItemList().addItem(new AtomicValue(result));
    rtx.moveTo(itemKey);
    return true;
  }
  /**
   * <p>
   * The effective boolean value of a value is defined as the result of applying
   * the fn:boolean function to the value, as defined in [XQuery 1.0 and XPath
   * 2.0 Functions and Operators].]
   * </p>
   * <p>
   * <li>If its operand is an empty sequence, fn:boolean returns false. </li>
   * <li>If its operand is a sequence whose first item is a node, fn:boolean
   * returns true. </li>
   * <li>If its operand is a singleton value of type xs:boolean or derived from
   * xs:boolean, fn:boolean returns the value of its operand unchanged. </li>
   * <li> If its operand is a singleton value of type xs:string, xs:anyURI,
   * xs:untypedAtomic, or a type derived from one of these, fn:boolean returns
   * false if the operand value has zero length; otherwise it returns true.
   * </li>
   * <li>If its operand is a singleton value of any numeric type or derived
   * from a numeric type, fn:boolean returns false if the operand value is NaN
   * or is numerically equal to zero; otherwise it returns true. </li>
   * <li>In all other cases, fn:boolean raises a type error [err:FORG0006].</li>
   * </p>
   * 
   * @param rtx
   *          the transaction to operate on.
   * @param axis
   *          Expression to get the effective boolean value for
   * @return err:FORG0006
   */
  public static boolean fnBoolean(final IReadTransaction rtx, final IAxis axis) {

    boolean ebv = ebv(axis);
    int itemKey = rtx.getItemList().addItem(new AtomicValue(ebv));
    rtx.moveTo(itemKey);
    return true;
  }

  /**
   * fn:data takes a sequence of items and returns a sequence of atomic values.
   * The result of fn:data is the sequence of atomic values produced by applying
   * the following rules to each item in the input sequence: If the item is an
   * atomic value, it is returned. If the item is a node, its typed value is
   * returned (err:FOTY0012 is raised if the node has no typed value.)
   * 
   * @param rtx
   *          the transaction to operate on
   * @param axis
   *          The sequence to atomize.
   * @return true, if an atomic value can be returned
   */
  public static boolean fnData(final IReadTransaction rtx, final IAxis axis) {

    if (axis.hasNext()) {

      if (rtx.getNodeKey() >= 0) {
        // set to typed value
        // if has no typed value
        // TODO // throw new XPathError(FOTY0012);

        int itemKey = rtx.getItemList().addItem(
            new AtomicValue(TypedValue.getBytes(rtx.getValue()), rtx
                .getTypeKey()));
        rtx.moveTo(itemKey);
        return true;
      } else {
        // return current item -> do nothing
        return true;
      }
    } else {
      // no more items.
      return false;
    }

  }

  

  /**
   * <p>
   * fn:nilled($arg as node()?) as xs:boolean?
   * </p>
   * <p>
   * Returns an xs:boolean indicating whether the argument node is "nilled". If
   * the argument is not an element node, returns the empty sequence. If the
   * argument is the empty sequence, returns the empty sequence.
   * </p>
   * 
   * @param rtx
   *          the transaction to operate on
   * @param axis
   *          The sequence containing the node to test its nilled property
   * @return true, if current item is a node that has the nilled property (only
   *         elements)
   */
  public static boolean fnNilled(final IReadTransaction rtx, final IAxis axis) {

    if (axis.hasNext() && rtx.isElementKind()) {
      boolean nilled = false; // TODO how is the nilled property defined?
      int itemKey = rtx.getItemList().addItem(new AtomicValue(nilled));
      rtx.moveTo(itemKey);
      return true;
    }
    return false; // empty sequence
  }

  /**
   * <p>
   * fn:node-name($arg as node()?) as xs:QName?
   * </p>
   * <p>
   * Returns an expanded-QName for node kinds that can have names. For other
   * kinds of nodes it returns the empty sequence. If $arg is the empty
   * sequence, the empty sequence is returned.
   * <p>
   * 
   * @param rtx
   *          the transaction to operate on
   * @param axis
   *          The sequence, containing the node, to return its QName
   * @return true, if node has a name
   */
  public static boolean fnNodeName(final IReadTransaction rtx, final IAxis axis) {

    if (axis.hasNext()) {

      String name = rtx.getName();
      if (!name.equals("-1")) {
        int itemKey = rtx.getItemList().addItem(
            new AtomicValue(name, Type.STRING));
        rtx.moveTo(itemKey);
        return true;
      }
    }
    // node has no name or axis is empty sequence
    // TODO: check if -1 really is the null-name-key
    return false;

  }

  public static boolean fnnot(final IReadTransaction rtx, final IAxis axis) {

    if (axis.hasNext()) {
      IItem item = new AtomicValue(!(Boolean.parseBoolean(rtx.getValue())));
      int itemKey = rtx.getItemList().addItem(item);
      rtx.moveTo(itemKey);
      return true;
    } else {
      return false;
    }

  }


  /**
   * Returns the value of the context item after atomization converted to an
   * xs:double.
   */
  public static boolean fnnumber(final IReadTransaction rtx) {

    // TODO: add error handling
    IItem item = new AtomicValue(TypedValue.getBytes(rtx.getValue()), rtx
        .keyForName("xs:double"));
    int itemKey = rtx.getItemList().addItem(item);
    rtx.moveTo(itemKey);

    return true;
  }


  public static AtomicValue not(final AtomicValue value) {

    return new AtomicValue(!Boolean.parseBoolean(TypedValue.parseString(value
        .getRawValue())));
  }

  public static boolean oneOrMore(final IReadTransaction rtx, final IAxis axis) {

    if (!axis.hasNext()) {
      throw new XPathError(ErrorType.FORG0004);
    } else {
      int itemKey = rtx.getItemList().addItem(new AtomicValue(true));
      rtx.moveTo(itemKey);

    }

    return true;
  }

  public static boolean sum(final IReadTransaction rtx, final IAxis axis) {

    Double value = 0.0;
    while (axis.hasNext()) {
      value = value + Double.parseDouble(rtx.getValue());
    }

    int itemKey = rtx.getItemList()
        .addItem(new AtomicValue(value, Type.DOUBLE));
    rtx.moveTo(itemKey);
    return true;
  }

  public static boolean sum(final IReadTransaction rtx, final IAxis axis,
      final IAxis zero) {

    Double value = 0.0;
    if (!axis.hasNext()) {
      zero.hasNext(); // if is empty sequence, return values specified for zero
    } else {
      do {
        value = value + Double.parseDouble(rtx.getValue());
      } while (axis.hasNext());
      int itemKey = rtx.getItemList().addItem(
          new AtomicValue(value, Type.DOUBLE));
      rtx.moveTo(itemKey);
    }
    return true;
  }

  public static boolean zeroOrOne(final IReadTransaction rtx, final IAxis axis) {

    boolean result = true;

    if (axis.hasNext()) {
      if (axis.hasNext()) { // more than one result
        throw new XPathError(ErrorType.FORG0003);
      }
    }

    int itemKey = rtx.getItemList().addItem(new AtomicValue(result));
    rtx.moveTo(itemKey);
    return true;
  }

}