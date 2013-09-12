/*
 * FeetStarter.h
 *
 *  Created on: 10 ????. 2013 ?.
 *      Author: il
 */

#ifndef FEETSTARTER_H_
#define FEETSTARTER_H_

#include <map>
#include <string>
#include <list>

namespace zetes
{
	namespace feet
	{
#ifdef __MINGW32__
		typedef std::wstring argstring;		// 2-byte UNICODE strings in Windows
#else
		typedef std::string argstring;		// UTF-8 encoded strings in POSIX systems
#endif

		class FeetStarter
		{
		private:
			std::string applicationClassName;
			int maximumHeapSizeMegabytes;
			std::map<std::string, std::string> definitions;
			std::list<argstring> arguments;
		public:
			FeetStarter();

			void setApplicationClassName(const std::string& applicationClassName);
			const std::string& getApplicationClassName() const;

			void setMaximumHeapSizeMegabytes(int maximumHeapSizeMegabytes);
			int getMaximumHeapSizeMegabytes() const;

			void setDefinition(const std::string& name, const std::string& value);
			const std::string* getDefinition(const std::string& name) const;

			std::list<argstring>& takeArguments();

			int run();

			virtual ~FeetStarter();
		};

	}
}
#endif
