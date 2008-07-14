#ifndef __LOGGING_HH
#define __LOGGING_HH

#define FALSE           0
#define TRUE            1

#ifndef LOGGING_ENABLED
#define LOGGING_ENABLED TRUE
#endif /* LOGGING_ENABLED */

/* <Logging macros and inline helper functions> */
enum loglevel {
  error = 0x0000010,
  debug = 0x0000100,
  trace = 0x1000000
};

/* Here you can set which log levels are printed to stderr. */
static int logMask = error | debug | trace;

static inline bool loglevelEnabled(loglevel ll) {
  return (ll & logMask) != 0x0;
}

static inline const _TCHAR* loglevelToString(loglevel ll) {
  switch(ll) {
  case error: return _T("ERROR");
  case debug: return _T("DEBUG");
  case trace: return _T("TRACE");
  default: return _T("[Unknown loglevel!]");
  }
}

/* Main logging macro. */
#if LOGGING_ENABLED
#define LOG(loglevel, format, ...) do { if(loglevelEnabled(loglevel)) { _ftprintf(stderr, _T("%s: ") _T(format) _T("\n"), loglevelToString(loglevel), ##__VA_ARGS__); fflush(stderr); } } while(0)
#else
#define LOG(loglevel, format, ...)
#endif
/* </Logging macros and inline helper functions> */

inline void setLogMask(int newMask) {
  logMask = newMask;
}

#endif
