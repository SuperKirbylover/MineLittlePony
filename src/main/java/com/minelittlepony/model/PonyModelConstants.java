package com.minelittlepony.model;

public final class PonyModelConstants {

    public static final float
        PI = (float)Math.PI,

        BODY_CENTRE_X = 0,
        BODY_CENTRE_Y = 8,
        BODY_CENTRE_Z = 6,

        NECK_CENTRE_X = BODY_CENTRE_X - 2,
        NECK_CENTRE_Y = BODY_CENTRE_Y - 6.8F,
        NECK_CENTRE_Z = BODY_CENTRE_Z - 8.8F,

        BODY_ROTATE_ANGLE_X_NOTSNEAK = 0,
        BODY_ROTATE_ANGLE_X_SNEAK = 0.4F,
        BODY_ROTATE_ANGLE_X_RIDING = PI * 3.8F,

        BODY_RP_Y_NOTSNEAK = 0,
        BODY_RP_Y_SNEAK = 7,
        BODY_RP_Y_RIDING = 1,

        BODY_RP_Z_NOTSNEAK = 0,
        BODY_RP_Z_SNEAK = -4,
        BODY_RP_Z_RIDING = 4,

        EXT_WING_ROTATE_ANGLE_X = 2.5F,

        FIRSTP_ARM_CENTRE_X = -1,
        FIRSTP_ARM_CENTRE_Y = 4,
        FIRSTP_ARM_CENTRE_Z = 0,

        FRONT_LEG_RP_Y_NOTSNEAK = 8,
        FRONT_LEG_RP_Y_SNEAK = 8,

        HEAD_CENTRE_X = 0,
        HEAD_CENTRE_Y = -1,
        HEAD_CENTRE_Z = -2,

        HEAD_RP_X = 0,
        HEAD_RP_Y = 0,
        HEAD_RP_Z = 0,

        HORN_X = HEAD_CENTRE_X - 0.5F,
        HORN_Y = HEAD_CENTRE_Y - 10,
        HORN_Z = HEAD_CENTRE_Z - 1.5F,

        LEFT_WING_EXT_RP_X = 4.5F,
        LEFT_WING_EXT_RP_Y = 6,
        LEFT_WING_EXT_RP_Z = 6,

        LEFT_WING_ROTATE_ANGLE_Z_SNEAK = 4,

        ROTATE_270 = 4.712F,
        ROTATE_90 = 1.571F,

        SNEAK_LEG_X_ROTATION_ADJUSTMENT = 0.4F,

        TAIL_RP_X = 0,
        TAIL_RP_Y = 0,
        TAIL_RP_Z = 0,

        TAIL_RP_Z_NOTSNEAK = 14,
        TAIL_RP_Z_SNEAK = 15,

        THIRDP_ARM_CENTRE_X = 0,
        THIRDP_ARM_CENTRE_Y = 10,
        THIRDP_ARM_CENTRE_Z = 0,

        WING_FOLDED_RP_Y = 13,
        WING_FOLDED_RP_Z = -3,

        NECK_ROT_X = 0.166F;
}
