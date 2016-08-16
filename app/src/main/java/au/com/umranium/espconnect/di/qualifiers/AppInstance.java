package au.com.umranium.espconnect.di.qualifiers;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;
import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Identifies an instance that needs to be injected once per the lifetime of the app for a type that is used across multiple scopes.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface AppInstance {
}
