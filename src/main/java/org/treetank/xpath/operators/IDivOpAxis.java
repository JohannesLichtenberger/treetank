package org.treetank.xpath.operators;

import org.treetank.api.IAxis;
import org.treetank.api.IItem;
import org.treetank.api.IReadTransaction;
import org.treetank.utils.TypedValue;
import org.treetank.xpath.AtomicValue;
import org.treetank.xpath.functions.XPathError;
import org.treetank.xpath.functions.XPathError.ErrorType;
import org.treetank.xpath.types.Type;

/**
 * <h1>AddOpAxis</h1>
 * <p>
 * Performs an arithmetic integer division on two input operators.
 * </p>
 * 
 * @author Tina Scherer
 */
public class IDivOpAxis extends AbstractOpAxis {

  /**
   * Constructor. Initializes the internal state.
   * 
   * @param rtx
   *          Exclusive (immutable) trx to iterate with.
   * @param op1
   *          First value of the operation
   * @param op2
   *          Second value of the operation
   */
  public IDivOpAxis(final IReadTransaction rtx, final IAxis op1, 
      final IAxis op2) {

    super(rtx, op1, op2);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IItem operate(final AtomicValue operand1, final AtomicValue operand2) {

    Type returnType = getReturnType(operand1.getTypeKey(), operand2
        .getTypeKey());
    int typeKey = getTransaction().keyForName(returnType.getStringRepr());

    final byte[] value;

   
    try {
      final int op1 = (int) Double.parseDouble(
          TypedValue.parseString(operand1.getRawValue())); 
      final int op2 = (int) Double.parseDouble(
          TypedValue.parseString(operand2.getRawValue())); 
      final int iValue = op1 / op2;
          value = TypedValue.getBytes(iValue);
          return new AtomicValue(value, typeKey);
        } catch (ArithmeticException e) {
          throw new XPathError(ErrorType.FOAR0001);
        }
        

  }

  /**
   * {@inheritDoc}
   */
  protected Type getReturnType(final int op1, final int op2) {

    Type type1;
    Type type2;
    try {
      type1 = Type.getType(op1).getPrimitiveBaseType();
      type2 = Type.getType(op2).getPrimitiveBaseType();
    } catch (IllegalStateException e) {
      throw new XPathError(ErrorType.XPTY0004);
    }

    if (type1.isNumericType() && type2.isNumericType()) {
      
      return Type.INTEGER;
    } else {
      
          throw new XPathError(ErrorType.XPTY0004);
      
      
    }
  }

}