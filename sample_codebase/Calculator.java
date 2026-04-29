package sample_codebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculator with caching to demonstrate more complex code structure.
 */
public class Calculator {
    private Map<String, Integer> cache = new HashMap<>();
    
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
    
    public int multiply(int a, int b) {
        String key = a + "*" + b;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        int result = a * b;
        cache.put(key, result);
        return result;
    }
    
    public double divide(int a, int b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return (double) a / b;
    }
    
    public void clearCache() {
        cache.clear();
    }
}
