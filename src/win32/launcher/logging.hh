#ifndef __LOGGING_HH
#define __LOGGING_HH

#define FALSE           0
#define TRUE            1

#ifndef LOGGING_ENABLED
#define LOGGING_ENABLED TRUE
#endif /* LOGGING_ENABLED */

/** Format placeholder to use for _TCHAR strings in log messages. */
#ifdef _UNICODE
#define FMTts "S"
#else
#define FMTts "s"
#endif

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
static inline void LOG(enum loglevel loglevel, const char *format, ...)
{
  if(loglevelEnabled(loglevel)) {
    va_list ap;
    va_start(ap, format);
    _ftprintf(stderr, _T("%s: "), loglevelToString(loglevel));
    vfprintf(stderr, format, ap);
    _ftprintf(stderr, _T("\n"));
    fflush(stderr);
    va_end(ap);
  }
}
#else
static inline void LOG(enum loglevel loglevel, const char *format, ...) {}
#endif
/* </Logging macros and inline helper functions> */

inline void setLogMask(int newMask) {
  logMask = newMask;
}

#endif
