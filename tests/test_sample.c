#include <stdlib.h>
#include "unity/unity.h"

void initialise_monitor_handles();

void setUp(void) {}
void tearDown(void) {}

static void test_2x2_still_eq_4(void) {
    int val = 2*2;
    TEST_ASSERT_EQUAL_INT(4, val);
}

static void test_2x2_still_ne_5(void) {
    TEST_ASSERT_EQUAL_INT(5, 2*2);
}

int main(void) {
    initialise_monitor_handles();
    UnityBegin(__FILE__);
    RUN_TEST(test_2x2_still_eq_4);
    RUN_TEST(test_2x2_still_ne_5);
    exit(UnityEnd());
}