package ru.sibdigital.addcovid;

import lombok.Data;

@Data
public class AssertsAccumulator
{
//    private StringBuilder errors;
//    private boolean assertsPassed;
//
//
//
//    public AssertsAccumulator()
//    {
//        errors = new StringBuilder();
//        assertsPassed = true;
//    }
//
//    private void RegisterError(String exceptionMessage)
//    {
//        assertsPassed = false;
//        errors.append(exceptionMessage);
//    }
//
//    public AssertsAccumulator accumulate(Assertions asserting)
//    {
//        try
//        {
//            asserting.Invoke();
//        }
//        catch (Exception exception)
//        {
//            RegisterError(exception.Message);
//        }
//    }
//
//    public AssertsAccumulator release()
//    {
//        if (!assertsPassed)
//        {
//            throw new AssertionException(AccumulatedErrorMessage);
//        }
//    }
}