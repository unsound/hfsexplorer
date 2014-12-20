#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=g77
AS=

# Macros
CND_PLATFORM=MinGW-Windows
CND_CONF=Debug
CND_DISTDIR=dist

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/llio_common.o \
	${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/launcher/launcher.o \
	${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/org_catacombae_storage_io_win32_Win32FileStream.o \
	${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/org_catacombae_storage_io_win32_ReadableWin32FileStream.o

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-Debug.mk dist/Debug/MinGW-Windows/libHFSExplorerNative.dll

dist/Debug/MinGW-Windows/libHFSExplorerNative.dll: ${OBJECTFILES}
	${MKDIR} -p dist/Debug/MinGW-Windows
	${LINK.cc} -shared -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libHFSExplorerNative.dll ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/llio_common.o: nbproject/Makefile-${CND_CONF}.mk ../../src/win32/llio/llio_common.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio
	${RM} $@.d
	$(COMPILE.c) -g  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/llio_common.o ../../src/win32/llio/llio_common.c

${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/launcher/launcher.o: nbproject/Makefile-${CND_CONF}.mk ../../src/win32/launcher/launcher.cpp 
	${MKDIR} -p ${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/launcher
	${RM} $@.d
	$(COMPILE.cc) -g  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/launcher/launcher.o ../../src/win32/launcher/launcher.cpp

${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/org_catacombae_storage_io_win32_Win32FileStream.o: nbproject/Makefile-${CND_CONF}.mk ../../src/win32/llio/org_catacombae_storage_io_win32_Win32FileStream.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio
	${RM} $@.d
	$(COMPILE.c) -g  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/org_catacombae_storage_io_win32_Win32FileStream.o ../../src/win32/llio/org_catacombae_storage_io_win32_Win32FileStream.c

${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/org_catacombae_storage_io_win32_ReadableWin32FileStream.o: nbproject/Makefile-${CND_CONF}.mk ../../src/win32/llio/org_catacombae_storage_io_win32_ReadableWin32FileStream.c 
	${MKDIR} -p ${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio
	${RM} $@.d
	$(COMPILE.c) -g  -MMD -MP -MF $@.d -o ${OBJECTDIR}/_ext/_DOTDOT/_DOTDOT/src/win32/llio/org_catacombae_storage_io_win32_ReadableWin32FileStream.o ../../src/win32/llio/org_catacombae_storage_io_win32_ReadableWin32FileStream.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r build/Debug
	${RM} dist/Debug/MinGW-Windows/libHFSExplorerNative.dll

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
