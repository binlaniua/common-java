import com.github.binlaniua.common.valid.DBLength;
import lombok.Data;
import org.junit.Test;

import javax.persistence.Column;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidatorTest {

    @Data
    private static class T {

        @Column(length = 1)
        @DBLength(entity = T.class, column = "a")
        private String a;

        @Column(columnDefinition = "varchar(2) comment '11'")
        @DBLength(entity = T.class, column = "b")
        private String b;
    }

    @Test
    public void testDBLength() {
        // 生成校验器
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();

        final T t = new T();
        t.setA("111111");
        t.setB("333333");

        // 校验 person
        final Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        // 输出校验结果
        System.out.println("错误数量: " + constraintViolations.size());
        for (final ConstraintViolation<T> v : constraintViolations) {
            System.out.println(v.getMessage());
        }
    }
}
