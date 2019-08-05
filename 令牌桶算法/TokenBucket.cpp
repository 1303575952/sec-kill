#include "TokenBucket.h"

tokenbucket::tokenbucket():quit_(false),stop_(false), nanoseconds_(50),maxtoken_(20),token_(0)
{
	thr = thread([ & ] {
		while (!quit_)
		{
			while (stop_);
			std::this_thread::sleep_until(high_clock::now() + nanoseconds(nanoseconds_));
			{
				lock_guard lock_(mut_);
				if (token_ < maxtoken_)
					++token_;
			}
			cv_.notify_one();
		}
	});
}
tokenbucket::~tokenbucket()
{
	{
		lock_guard lock_(mut_);
		quit_ = false;
		stop_ = false;
	}
	cv_.notify_all();
	if (thr.joinable())
		thr.join();
}
tokenbucket& tokenbucket::getInstance()
{
	static tokenbucket instance{};
	return instance;
}
void tokenbucket::beginWork()
{
	lock_guard lock_(mut_);
	stop_ = false;
}
void tokenbucket::stopWork()
{
	lock_guard lock_(mut_);
	stop_ = true;
}
void tokenbucket::setMaxToken(size_t maxtoken)
{
	lock_guard lock_(mut_);
	maxtoken_ = maxtoken;
}
void tokenbucket::setPermitsPerSecond(size_t permitsPerSecond)
{
	lock_guard lock_(mut_);
	nanoseconds_ = 1000000000 / permitsPerSecond;
	if (1000000000 % permitsPerSecond > 0)
		++nanoseconds_;
}
bool tokenbucket::acquire()
{
	unique_lock lock_(mut_);
	while (!quit_)
	{
		if (stop_)
			return false;
		cv_.wait(lock_, [ & ] {return quit_ || stop_ || token_ > 0; });
		if (token_ > 0)
		{
			--token_;
			return true;
		}
	}
	return false;
}
bool tokenbucket::nonblockacquire()
{
	lock_guard lock_(mut_);
	if (token_ > 0)
	{
		--token_;
		return true;
	}
	return false;
}
void tokenbucket::quit()
{
	{
		lock_guard lock_(mut_);
		stop_ = true;
		quit_ = true;
	}
	cv_.notify_all();	
}
size_t tokenbucket::getMaxToken() const
{
	return maxtoken_;
}
size_t tokenbucket::getnumpersecond()
{
	return 1000000000 / nanoseconds_;
}