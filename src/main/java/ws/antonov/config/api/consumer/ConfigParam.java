package ws.antonov.config.api.consumer;

import java.lang.annotation.*;

/**
 * @author aantonov
 * Created On: Oct 20, 2010
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigParam {
    /** The config parameter name to bind annotated value to. */
	String value() default "";
}
