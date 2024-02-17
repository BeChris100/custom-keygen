#include <iostream>
#include <string>
#include <vector>
#include <random>
#include <thread>
#include <sstream>
#include <iomanip>

std::string mkHexNum(int length, int divisor) {
	std::random_device rd;
	std::mt19937 gen(rd());
	std::uniform_int_distribution<> dis(0, 15);

	bool valid = false;
	std::string number;

	while (!valid) {
		std::stringstream ss;
		bool hasDigit = false, hasAlpha = false;
		for (int i = 0; i < length; ++i) {
			int digit = dis(gen);
			if (digit < 10) hasDigit = true;
			else hasAlpha = true;

			ss << std::hex << digit;
		}

		number = ss.str();
		unsigned long l = std::stoul(number, nullptr, 16);
		if (hasDigit && hasAlpha && l % divisor == 0)
			valid = true;
	}

	return number;
}

void mkKey(const std::vector<int>& divisors, const std::vector<int>& lengths) {
	std::vector<std::string> parts;
	for (size_t i = 0; i < divisors.size(); ++i) {
		parts.push_back(mkHexNum(lengths[i], divisors[i]));
	}

	std::cout << "1983-" << parts[0] << "-" << parts[1] << "-" << parts[2] << std::endl;
}

void mkKeys(const std::vector<int>& divisors, const std::vector<int>& lengths) {
	while (true) {
		mkKey(divisors, lengths);
	}
}

int main() {
	const std::vector<int> divisors = {27, 16, 21};
	const std::vector<int> lengths = {9, 4, 7};

	std::vector<std::thread> threads;
	for (int i = 0; i < 1500; i++) {
		threads.emplace_back([&divisors, &lengths]() { mkKeys(divisors, lengths); });
	}

	for (auto& thread : threads)
		thread.join();

	return 0;
}


