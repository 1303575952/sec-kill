#ifndef _TOKENBUCKET_H_
#define _TOKENBUCKET_H_

#include <mutex>
#include <condition_variable>
#include <chrono>
#include <thread>

class tokenbucket
{
	using mutex = typename std::mutex;
	using condition_variable = typename std::condition_variable;
	using thread = typename std::thread;
	using high_clock = typename std::chrono::high_resolution_clock;
	using nanoseconds = typename std::chrono::nanoseconds;
	using lock_guard = typename std::lock_guard<typename mutex>;
	using unique_lock = typename std::unique_lock<typename mutex>;
public:
	tokenbucket(const tokenbucket&) = delete;
	tokenbucket operator=(const tokenbucket&)=delete;
	static tokenbucket& getInstance();
	//阻塞获取令牌
	bool acquire();
	//非阻塞获取令牌
	bool nonblockacquire();
	//permitsPerSecond:每秒钟生成令牌的个数
	void setPermitsPerSecond(size_t permitsPerSecond);
	void quit();
	//maxtoken:令牌桶中的最大令牌个数
	void setMaxToken(size_t maxtoken);
	//获取令牌桶的最大令牌数
	size_t getMaxToken()const;
	//获取令牌桶每秒生成的令牌个数
	size_t getnumpersecond();
	//令牌桶开始生成并分发令牌
	void beginWork();
	//令牌桶开始停止工作
	void stopWork();	
protected:    
	tokenbucket();
	~tokenbucket();
private:
	bool quit_;//令牌桶退出标识符，一旦退出，整个令牌桶不再可用
	bool stop_;//令牌桶停止工作标识符	
	size_t nanoseconds_;//产生令牌的周期，单位为ms
	size_t maxtoken_;//最大令牌个数
	size_t token_;//当前令牌桶的令牌个数
	mutex mut_;
	condition_variable cv_;
	thread thr;
};



#endif
