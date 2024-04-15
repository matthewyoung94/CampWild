//package com.example.campwild;
//
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
//public class LoginActivityTest {

//    private LoginActivity loginActivity;
//    private boolean mapsActivityStarted;
//
//    @Before
//    public void setUp() {
//        loginActivity = new LoginActivity();
//        mapsActivityStarted = false;
//    }
//
//    @Test
//    public void testSignInWithEmailAndPassword_Success() {
//        loginActivity.signIn("test@example.com", "password", new LoginActivity.SignInCallback() {
//            @Override
//            public void onSignInSuccess() {
//                mapsActivityStarted = true;
//            }
//
//            @Override
//            public void onSignInFailed() {
//                fail("Sign-in should not fail");
//            }
//        });
//
//        assertTrue(mapsActivityStarted);
//    }
//
//    @Test
//    public void testSignInWithEmailAndPassword_Failure() {
//        loginActivity.signIn("test@example.com", "incorrectPassword", new LoginActivity.SignInCallback() {
//            @Override
//            public void onSignInSuccess() {
//                fail("Sign-in should fail");
//            }
//
//            @Override
//            public void onSignInFailed() {
//                // Expected failure
//            }
//        });
//
//        assertFalse(mapsActivityStarted);
//    }
//
//    @Test
//    public void testSignUpWithEmailAndPassword_Success() {
//        loginActivity.signUp("newuser@example.com", "password", new LoginActivity.SignUpCallback() {
//            @Override
//            public void onSignUpSuccess() {
//                mapsActivityStarted = true;
//            }
//
//            @Override
//            public void onSignUpFailed() {
//                fail("Sign-up should not fail");
//            }
//        });
//
//        assertTrue(mapsActivityStarted);
//    }
//
//    @Test
//    public void testSignUpWithEmailAndPassword_Failure() {
//        loginActivity.signUp("existinguser@example.com", "password", new LoginActivity.SignUpCallback() {
//            @Override
//            public void onSignUpSuccess() {
//                fail("Sign-up should fail");
//            }
//
//            @Override
//            public void onSignUpFailed() {
//                // Expected failure
//            }
//        });
//
//        assertFalse(mapsActivityStarted);
//    }
//
//    @Test
//    public void testToggleMode() {
//        assertTrue(loginActivity.isSignInMode()); // Initially sign-in mode
//
//        loginActivity.toggleMode();
//
//        assertFalse(loginActivity.isSignInMode()); // After toggling, should be in sign-up mode
//    }
//}
