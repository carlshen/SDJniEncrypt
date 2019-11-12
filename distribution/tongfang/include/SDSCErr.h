
// SDSCErr.h
#ifndef _SDSC_ERROR_H
#define _SDSC_ERROR_H


//------------------------------------------------------------
// Specifies error codes for communication.
//------------------------------------------------------------
#ifndef SDSC_COMMUNICATION_ERROR_DEFINE
#define SDSC_COMMUNICATION_ERROR_DEFINE

// The base error code that we defined.
#define SDSC_ERROR_BASE                      0x0F000000

// The parameter is incorrect.
#define SDSC_PARAM_ERROR                     0x0F000001

// The unexpected error has occurred.
#define SDSC_UNKNOWN_ERROR                   0x0F000002

// Not enough storage is available to process this command. Its meaning is the same as ERROR_NOT_ENOUGH_MEMORY in windows API.
#define SDSC_NO_MEMORY_ERROR                 0x0F000003

// The output of the function is too large to fit in the supplied buffer. More data is available.
// Its meaning is the same as ERROR_MORE_DATA in windows API.
#define SDSC_BUFFER_SMALL_ERROR              0x0F000004

// Some problem has occurred to the device, and the accurate error code can't be given.
#define SDSC_DEV_OP_ERROR                    0x0F000005

// The device is not ready. It means the device hasn't been reset in general.
#define SDSC_DEV_NOT_READY_ERROR             0x0F000006

// The header that is read from the device is incorrect, it means the device is not our device.
#define SDSC_DEV_HEADER_ERROR                0x0F000007

// The device is in the state of run time issue.The failed APDU (COS) command needs to be re-transmitted.
#define SDSC_DEV_RUN_TIME_ERROR              0x0F000008

// The specified data is incorrect.
#define SDSC_DATA_ERROR                      0x0F000009

// The length of the specified data is incorrect.
#define SDSC_DATA_LEN_ERROR                  0x0F00000A

// This function is not supported.
#define SDSC_NOT_SUPPORT_FUN_ERROR           0x0F00000B

// The state of the device is incorrect.
#define SDSC_DEV_STATE_ERROR                 0x0F00000C

// The device is set as writing protection.
#define SDSC_DEV_WRITE_PROTECT_ERROR         0x0F00000D

// The device is not exist.
#define SDSC_DEV_NOT_EXIST_ERROR             0x0F00000E


// The error codes that have not been listed before:

// The base error code that we defined when communication error between the application and the device has occurred.
#define SDSC_DEV_COMMUNICATION_BASE_ERROR    0x0FF00000
// SDSC_DEV_COMMUNICATION_BASE_ERROR++abnormal communication state code:
// It means returning 0x0FF0XXXX, the lower two bytes is the accurate communication error code.

#endif    // end: #define SDSC_COMMUNICATION_ERROR_DEFINE


#ifndef SDSA_COMMUNICATION_ERROR_DEFINE
#define SDSA_COMMUNICATION_ERROR_DEFINE

// The length of the specified password is incorrect.
#define SDSA_INPUT_PWD_LEN_ERROR           0x0F000061

// The specified password is incorrect.
#define SDSA_DEV_PWD_ERROR                 0x0F000062

// The specified password is invalid.
#define SDSA_INVALID_DEV_PWD_ERROR         0x0F000063

// Switch disk error.
#define SDSA_SWITCH_DISK_ERROR             0x0F000064

// The disk is already safe area.
#define SDSA_DISK_IS_SAFE_AREA_ERROR       0x0F000065

// The disk is already normal area.
#define SDSA_DISK_IS_NORMAL_AREA_ERROR     0x0F000066


#endif	// end: #define SDSA_COMMUNICATION_ERROR_DEFINE



#endif
